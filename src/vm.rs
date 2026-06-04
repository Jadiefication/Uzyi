//! Uzyi virtual machine core state and execution.
//!
//! This module defines the [`VM`] structure and its execution methods. The VM is an
//! 8-bit machine with 256 bytes of memory and 8 general-purpose registers (R0..R7).
//! R7 acts as the stack pointer.

use crate::dispatch::TABLE;
use crate::status::Status;
use crate::status::Status::Running;
use std::time::{Duration, SystemTime, SystemTimeError};

/// Number of general-purpose registers (R0..R7).
const REGISTERS: usize = 8;
/// Total size of VM memory in bytes.
const MEM_SIZE: usize = 256;

/// The `VM` struct represents the state of the virtual machine.
///
/// It contains registers, memory, program counter, and status flags.
pub struct VM {
    /// General-purpose registers (R0-R7).
    pub registers: [i8; REGISTERS],
    /// Memory space of the virtual machine.
    pub memory: [u8; MEM_SIZE],
    /// Program counter pointing to the next instruction in memory.
    pub counter: usize,
    /// Execution status of the VM.
    pub status: Status,
    /// Carry flag for arithmetic operations.
    pub cf: bool,
    /// Zero flag for comparison and arithmetic operations.
    pub zf: bool,
    /// Time when the VM instance started (used by `sleep` and timing SYS reads).
    pub start_time: SystemTime,
    /// Number of executed cycles (exposed via SYS address 0xFC as an 8-bit value).
    pub cycles: usize
}

impl VM {
    /// Creates a new `VM` instance with the provided instructions loaded into memory.
    ///
    /// - Initializes registers to 0 and sets R7 (stack pointer) to -1 (top of stack at 0xFF).
    /// - Sets the program counter to 0 and status to `Running`.
    pub fn new(instructions: [u8; MEM_SIZE]) -> Self {
        let mut registers = [0; 8];
        registers[7] = -1;
        Self {
            registers,
            memory: instructions,
            counter: 0,
            status: Running,
            cf: false,
            zf: false,
            start_time: SystemTime::now(),
            cycles: 0
        }
    }

    /// Executes a single instruction if possible.
    ///
    /// - If the VM is `Stopped`, does nothing.
    /// - If `Sleeping(until)`, yields if current time is before `until`, otherwise resumes `Running`.
    /// - Otherwise, fetch-decodes-executes one instruction and increments `cycles`.
    pub fn step(&mut self) -> Result<(), SystemTimeError> {
        if self.status == Status::Stopped { return Ok(()); }
        if let Status::Sleeping(wake_at) = self.status {
            if self.start_time.elapsed()? >= Duration::from_millis(wake_at) {
                self.status = Running;
            } else {
                std::thread::yield_now();
                return Ok(())
            }
        }

        let instruction = self.get_mem(self.counter);
        self.counter += 1;

        TABLE[instruction as usize](self);

        self.cycles += 1;
        Ok(())
    }

    /// Starts continuous execution of the virtual machine.
    ///
    /// Loops calling [`step`] until the VM status becomes `Stopped`.
    pub fn run(&mut self) -> Result<(), SystemTimeError> {
        while self.status != Status::Stopped {
            self.step()?
        }
        Ok(())
    }

    /// Returns a copy of the general-purpose registers.
    pub fn get_registers(&self) -> [i8; REGISTERS] {
        self.registers
    }

    /// Retrieves a byte from memory at the specified index.
    ///
    /// Special SYS-mapped addresses:
    /// - `0xFC`: returns the low 8 bits of the `cycles` counter
    /// - `0xFD`: returns a coarse timer (microseconds since start, truncated to 8 bits)
    ///
    /// # Panics
    /// Panics if the index is out of bounds (>= 256).
    pub fn get_mem(&self, index: usize) -> u8 {
        if index >= MEM_SIZE {
            panic!("Out of bounds")
        } else if index == 0xFC {
            self.cycles as u8
        } else if index == 0xFD {
            self.start_time.elapsed().unwrap_or(Duration::new(0, 0)).as_micros() as u8
        } else {
            self.memory[index]
        }
    }

    /// Retrieves the value of a register at the specified index.
    /// Index must be within `0..REGISTERS`.
    pub fn get_reg(&self, index: usize) -> i8 {
        self.registers[index]
    }

}
