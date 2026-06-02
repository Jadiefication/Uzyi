pub const MOV: usize = 0x00;
pub const MOVR: usize = 0x01;

pub const ADD: usize = 0x02;
pub const SUB: usize = 0x03;
pub const MUL: usize = 0x04;
pub const DIV: usize = 0x05;

pub const INC: usize = 0x06;
pub const DEC: usize = 0x07;

pub const AND: usize = 0x08;
pub const OR: usize = 0x09;
pub const NOT: usize = 0x0A;
pub const XOR: usize = 0x0B;

pub const SHL: usize = 0x0C;
pub const SHR: usize = 0x0D;

pub const LOAD: usize = 0x0E;
pub const STORE: usize = 0x0F;

pub const CMP: usize = 0x10;
pub const BEQ: usize = 0x11;
pub const BLO: usize = 0x12;
pub const BHI: usize = 0x13;
pub const BLEQ: usize = 0x14;
pub const BHEQ: usize = 0x15;
pub const B: usize = 0x16;

pub const PUSH: usize = 0x17;
pub const POP: usize = 0x18;

pub const HLT: usize = 0xFF;