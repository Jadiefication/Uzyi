use std::ops::Index;
use crate::dispatch::TABLE;

const REGISTERS: usize = 8;
const MEM_SIZE: usize = 256;

pub struct VM {
    pub registers: [i8; REGISTERS],
    pub memory: [u8; MEM_SIZE],
    pub counter: usize,
    pub running: bool,
    pub cf: bool,
    pub zf: bool
}

impl VM {
    pub fn new(instructions: [u8; MEM_SIZE]) -> Self {
        Self {
            registers: [0; 8],
            memory: instructions,
            counter: 0,
            running: true,
            cf: false,
            zf: false
        }
    }

    pub fn run(&mut self) {
        while self.running {
            let instruction = self[self.counter];
            self.counter += 1;

            if self.counter >= MEM_SIZE && self.running {
                panic!("Out of bounds")
            }

            TABLE[instruction as usize](self)
        }
    }

    pub fn get_registers(&self) -> [i8; REGISTERS] {
        self.registers
    }
}

impl Index<usize> for VM {
    type Output = u8;

    fn index(&self, index: usize) -> &Self::Output {
        if index >= MEM_SIZE {
            panic!("Out of bounds")
        } else {
            &self.memory[index]
        }
    }
}