use crate::dispatch::TABLE;
use crate::status::Status;
use crate::status::Status::Running;
use std::time::{Duration, SystemTime, SystemTimeError};

const REGISTERS: usize = 8;
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
    pub zf: bool
}

impl VM {
    /// Creates a new `VM` instance with the provided instructions loaded into memory.
    ///
    /// The stack pointer (R7) is initialized to -1.
    pub fn new(instructions: [u8; MEM_SIZE]) -> Self {
        let mut registers = [0; 8];
        registers[7] = -1;
        Self {
            registers,
            memory: instructions,
            counter: 0,
            status: Running,
            cf: false,
            zf: false
        }
    }

    /// Starts the execution of the virtual machine.
    ///
    /// It fetches, decodes, and executes instructions until the `running` flag is false.
    pub fn run(&mut self) -> Result<(), SystemTimeError> {
        if let Status::Sleeping(wake_at) = self.status {
            let time = SystemTime::now();
            let duration = Duration::from_millis(wake_at);
            if time.elapsed()?.max(duration) != duration {
                self.status = Running
            }
        }
        while self.status != Status::Stopped {
            if let Status::Sleeping(wake_at) = self.status {
                if SystemTime::now().elapsed()? >= Duration::from_millis(wake_at) {
                    self.status = Running;
                } else {
                    std::thread::yield_now();
                    continue;
                }
            }

            let instruction = self.get_mem(self.counter);
            self.counter += 1;

            if self.counter >= MEM_SIZE && self.status == Running {
                panic!("Out of bounds")
            }

            TABLE[instruction as usize](self)
        }
        Ok(())
    }

    /// Returns a copy of the general-purpose registers.
    pub fn get_registers(&self) -> [i8; REGISTERS] {
        self.registers
    }

    /// Retrieves a byte from memory at the specified index.
    ///
    /// # Panics
    ///
    /// Panics if the index is out of bounds.
    pub fn get_mem(&self, index: usize) -> u8 {
        if index >= MEM_SIZE {
            panic!("Out of bounds")
        } else {
            self.memory[index]
        }
    }

    /// Retrieves the value of a register at the specified index.
    pub fn get_reg(&self, index: usize) -> i8 {
        self.registers[index]
    }

}
