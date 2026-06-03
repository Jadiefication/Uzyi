/// Move immediate value to register: `MOV R, imm`
pub const MOV: usize = 0x00;
/// Move register to register: `MOVR R1, R2`
pub const MOVR: usize = 0x01;

/// Add two registers: `ADD R1, R2` (R1 = R1 + R2)
pub const ADD: usize = 0x02;
/// Subtract two registers: `SUB R1, R2` (R1 = R1 - R2)
pub const SUB: usize = 0x03;
/// Multiply two registers: `MUL R1, R2` (R1 = R1 * R2)
pub const MUL: usize = 0x04;
/// Divide two registers: `DIV R1, R2` (R1 = R1 / R2)
pub const DIV: usize = 0x05;

/// Increment register: `INC R`
pub const INC: usize = 0x06;
/// Decrement register: `DEC R`
pub const DEC: usize = 0x07;

/// Bitwise AND: `AND R1, R2`
pub const AND: usize = 0x08;
/// Bitwise OR: `OR R1, R2`
pub const OR: usize = 0x09;
/// Bitwise NOT: `NOT R`
pub const NOT: usize = 0x0A;
/// Bitwise XOR: `XOR R1, R2`
pub const XOR: usize = 0x0B;

/// Shift left: `SHL R`
pub const SHL: usize = 0x0C;
/// Shift right: `SHR R`
pub const SHR: usize = 0x0D;

/// Load from memory address: `LOAD R, addr`
pub const LOAD: usize = 0x0E;
/// Store to memory address: `STORE R, addr`
pub const STORE: usize = 0x0F;

/// Compare two registers: `CMP R1, R2`
pub const CMP: usize = 0x10;
/// Branch if equal: `BEQ addr`
pub const BEQ: usize = 0x11;
/// Branch if lower: `BLO addr`
pub const BLO: usize = 0x12;
/// Branch if higher: `BHI addr`
pub const BHI: usize = 0x13;
/// Branch if lower or equal: `BLEQ addr`
pub const BLEQ: usize = 0x14;
/// Branch if higher or equal: `BHEQ addr`
pub const BHEQ: usize = 0x15;
/// Unconditional branch: `B addr`
pub const B: usize = 0x16;

/// Push value to stack: `PUSH imm`
pub const PUSH: usize = 0x17;
/// Pop value from stack: `POP R`
pub const POP: usize = 0x18;
/// Call subroutine: `CALL addr`
pub const CALL: usize = 0x19;
/// Return from subroutine: `RET`
pub const RET: usize = 0x1A;

/// Add immediate to register: `ADDI R, imm`
pub const ADDI: usize = 0x1B;
/// Subtract immediate from register: `SUBI R, imm`
pub const SUBI: usize = 0x1C;
/// Multiply register by immediate: `MULI R, imm`
pub const MULI: usize = 0x1D;

/// Load from address in register: `LOADR R1, R2` (R1 = [R2])
pub const LOADR: usize = 0x1E;
/// Store to address in register: `STORER R1, R2` ([R2] = R1)
pub const STORER: usize = 0x1F;

pub const SLEEP: usize = 0x20;

/// Halt execution
pub const HLT: usize = 0xFF;