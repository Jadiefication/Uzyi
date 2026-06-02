use crate::instructions::{ADD, ADDI, AND, B, BEQ, BHEQ, BHI, BLEQ, BLO, CALL, CMP, DEC, DIV, HLT, INC, LOAD, LOADR, MOV, MOVR, MUL, MULI, NOT, OR, POP, PUSH, RET, SHL, SHR, STORE, STORER, SUB, SUBI, XOR};
use crate::vm::VM;
use std::sync::LazyLock;

fn empty(_vm: &mut VM) {}

/// Instruction dispatch table.
pub static TABLE: LazyLock<[fn(&mut VM); 256]> = LazyLock::new(|| {
    let mut table = [empty as fn(&mut VM); 256];

    table[MOV] = mov;
    table[MOVR] = movr;
    table[ADD] = add;
    table[SUB] = sub;
    table[MUL] = mul;
    table[DIV] = div;
    table[INC] = inc;
    table[DEC] = dec;

    table[AND] = and;
    table[OR] = or;
    table[NOT] = not;
    table[XOR] = xor;
    table[SHL] = shl;
    table[SHR] = shr;

    table[LOAD] = load;
    table[STORE] = store;
    table[CMP] = cmp;
    table[BEQ] = beq;
    table[BLO] = blo;
    table[BHI] = bhi;
    table[BLEQ] = bleq;
    table[BHEQ] = bheq;
    table[B] = b;

    table[PUSH] = push;
    table[POP] = pop;
    table[CALL] = call;
    table[RET] = ret;

    table[ADDI] = addi;
    table[SUBI] = subi;
    table[MULI] = muli;

    table[LOADR] = loadr;
    table[STORER] = storer;

    table[HLT] = hlt;

    table
});

/// MOV R, imm: Loads an immediate value into a register.
fn mov(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let value = vm.get_mem(vm.counter);
    vm.counter += 1;
    vm.registers[r_1] = value as i8;
}

/// MOVR R1, R2: Copies the value from R2 to R1.
fn movr(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_2].clone();
}

/// ADD R1, R2: Adds R2 to R1 and stores the result in R1.
fn add(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] + vm.registers[r_2]
}

/// SUB R1, R2: Subtracts R2 from R1 and stores the result in R1.
fn sub(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] - vm.registers[r_2]
}

/// MUL R1, R2: Multiplies R1 by R2 and stores the result in R1.
fn mul(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] * vm.registers[r_2]
}

/// DIV R1, R2: Divides R1 by R2 and stores the result in R1.
fn div(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] / vm.registers[r_2]
}

/// INC R: Increments the value in register R.
fn inc(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] += 1
}

/// DEC R: Decrements the value in register R.
fn dec(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] -= 1
}

/// AND R1, R2: Bitwise AND of R1 and R2, stores result in R1.
fn and(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] & vm.registers[r_2]
}

/// OR R1, R2: Bitwise OR of R1 and R2, stores result in R1.
fn or(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] | vm.registers[r_2]
}

/// NOT R: Bitwise NOT of R, stores result in R.
fn not(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = !vm.registers[r_1]
}

/// XOR R1, R2: Bitwise XOR of R1 and R2, stores result in R1.
fn xor(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] ^ vm.registers[r_2]
}

/// SHL R: Logical shift left of R by 1 bit.
fn shl(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] << 1
}

/// SHR R: Logical shift right of R by 1 bit.
fn shr(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] >> 1
}

/// LOAD R, addr: Loads a value from memory address `addr` into register R.
fn load(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let address = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.get_mem(address) as i8
}

/// STORE R, addr: Stores the value of register R into memory address `addr`.
fn store(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let address = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.memory[address] = vm.registers[r_1] as u8
}

/// CMP R1, R2: Compares R1 and R2, setting ZF and CF flags.
fn cmp(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;

    vm.cf = false;
    vm.zf = false;

    let val1 = vm.registers[r_1] as u8;
    let val2 = vm.registers[r_2] as u8;

    if val1 < val2 {
        vm.cf = true;
    }
    if val1 == val2 {
        vm.zf = true;
    }
}

/// BEQ addr: Branch to `addr` if the Zero Flag (ZF) is set.
fn beq(vm: &mut VM) {
    let address = vm.get_mem(vm.counter);
    vm.counter += 1;

    if vm.zf {
        vm.counter = address as usize;
        vm.cf = false;
        vm.zf = false;
    }
}

/// BLO addr: Branch to `addr` if the Carry Flag (CF) is set (val1 < val2).
fn blo(vm: &mut VM) {
    let address = vm.get_mem(vm.counter);
    vm.counter += 1;

    if vm.cf {
        vm.counter = address as usize;
        vm.cf = false;
        vm.zf = false;
    }
}

/// BHI addr: Branch to `addr` if neither CF nor ZF are set (val1 > val2).
fn bhi(vm: &mut VM) {
    let address = vm.get_mem(vm.counter);
    vm.counter += 1;

    if !vm.cf && !vm.zf {
        vm.counter = address as usize;
        vm.cf = false;
        vm.zf = false;
    }
}

/// BLEQ addr: Branch to `addr` if CF or ZF is set (val1 <= val2).
fn bleq(vm: &mut VM) {
    let address = vm.get_mem(vm.counter);
    vm.counter += 1;

    if vm.cf || vm.zf {
        vm.counter = address as usize;
        vm.cf = false;
        vm.zf = false;
    }
}

/// BHEQ addr: Branch to `addr` if CF is not set or ZF is set (val1 >= val2).
fn bheq(vm: &mut VM) {
    let address = vm.get_mem(vm.counter);
    vm.counter += 1;

    if !vm.cf || vm.zf {
        vm.counter = address as usize;
        vm.cf = false;
        vm.zf = false;
    }
}

/// B addr: Unconditional branch to `addr`.
fn b(vm: &mut VM) {
    let address = vm.get_mem(vm.counter);
    vm.counter += 1;
    vm.counter = address as usize;
    vm.cf = false;
    vm.zf = false;
}

/// PUSH imm: Pushes an immediate value onto the stack.
fn push(vm: &mut VM) {
    if (vm.get_reg(7) as usize) & 0xFF == vm.memory.len() + 1 {
        panic!("VM close to instructions")
    }
    let value = vm.get_mem(vm.counter);
    vm.counter += 1;
    vm.memory[(vm.get_reg(7) as usize) & 0xFF] = value;
    vm.registers[7] -= 1;
}

/// POP R: Pops a value from the stack into register R.
fn pop(vm: &mut VM) {
    if (vm.get_reg(7) as usize) & 0xFF == 255 {
        panic!("VM at top of stack")
    }
    let register = vm.get_mem(vm.counter);
    vm.counter += 1;
    vm.registers[7] += 1;
    vm.registers[register as usize] = vm.get_mem((vm.get_reg(7) as usize) & 0xFF) as i8;
}

/// CALL addr: Pushes the next instruction address and branches to `addr`.
fn call(vm: &mut VM) {
    let address = vm.get_mem(vm.counter);
    vm.counter += 1;
    if (vm.get_reg(7) as usize) & 0xFF == vm.memory.len() + 1 {
        panic!("VM close to instructions")
    }
    vm.memory[(vm.get_reg(7) as usize) & 0xFF] = vm.counter as u8;
    vm.registers[7] -= 1;
    vm.counter = address as usize;
}

/// RET: Pops the return address from the stack and returns to it.
fn ret(vm: &mut VM) {
    if (vm.get_reg(7) as usize) & 0xFF == 255 {
        panic!("VM at top of stack")
    }
    vm.registers[7] += 1;
    vm.counter = vm.memory[(vm.get_reg(7) as usize) & 0xFF] as usize;
}

/// ADDI R, imm: Adds an immediate value to register R.
fn addi(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let value = vm.get_mem(vm.counter) as i8;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1].wrapping_add(value);
}

/// SUBI R, imm: Subtracts an immediate value from register R.
fn subi(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let value = vm.get_mem(vm.counter) as i8;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1].wrapping_sub(value);
}

/// MULI R, imm: Multiplies register R by an immediate value.
fn muli(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let value = vm.get_mem(vm.counter) as i8;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1].wrapping_mul(value);
}

/// LOADR R1, R2: Loads a value from memory at the address stored in R2 into R1.
fn loadr(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;

    let target_address = (vm.registers[r_2] as usize) & 0xFF;
    vm.registers[r_1] = vm.get_mem(target_address) as i8;
}

/// STORER R1, R2: Stores the value of R1 into memory at the address stored in R2.
fn storer(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;

    let target_address = (vm.registers[r_2] as usize) & 0xFF;
    vm.memory[target_address] = vm.registers[r_1] as u8;
}

/// HLT: Halts VM execution.
fn hlt(vm: &mut VM) {
    vm.running = false
}