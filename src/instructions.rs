const MOV: usize = 0x00;

const ADD: usize = 0x01;
const SUB: usize = 0x02;
const MUL: usize = 0x03;
const DIV: usize = 0x04;

const INC: usize = 0x05;
const DEC: usize = 0x06;

const AND: usize = 0x07;
const OR: usize = 0x08;
const NOT: usize = 0x09;
const XOR: usize = 0x0A;

const SHL: usize = 0x0B;
const SHR: usize = 0x0C;

const LOAD: usize = 0x0D;
const STORE: usize = 0x0E;

const JMP: usize = 0x0F;

const CMP: usize = 0x10;
const BEQ: usize = 0x11;
const BLO: usize = 0x12;
const BHI: usize = 0x13;
const BLEQ: usize = 0x14;
const BHEQ: usize = 0x15;
const B: usize = 0x16;

const HLT: usize = 0xFF;