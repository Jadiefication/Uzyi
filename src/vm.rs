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
    fn new(instructions: [u8; MEM_SIZE]) -> Self {
        Self {
            registers: [0; 8],
            memory: instructions,
            counter: 0,
            running: true,
            cf: false,
            zf: false
        }
    }

    fn run(&mut self) {
        while self.running {
            let instruction = self.memory[self.counter];
            self.counter += 1;

            if self.counter >= MEM_SIZE {
                panic!("Out of bounds")
            }

            TABLE[instruction as usize](self)
        }
    }
}