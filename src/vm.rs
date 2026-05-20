const REGISTERS: usize = 8;
const MEM_SIZE: usize = 256;

struct VM {
    registers: [i8; REGISTERS],
    memory: [u8; MEM_SIZE],
    counter: usize,
    running: bool
}

impl VM {
    fn new(instructions: [u8; MEM_SIZE]) -> Self {
        Self {
            registers: [0; 8],
            memory: instructions,
            counter: 0,
            running: true
        }
    }
}