use crate::instructions::{ADD, AND, B, BEQ, BHEQ, BHI, BLEQ, BLO, CALL, CMP, DEC, DIV, HLT, INC, LOAD, MOV, MOVR, MUL, NOT, OR, POP, PUSH, RET, SHL, SHR, STORE, SUB, XOR};
use crate::vm::VM;
use std::sync::LazyLock;

fn empty(_vm: &mut VM) {}

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

    table[HLT] = hlt;

    table
});

fn mov(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let value = vm.get_mem(vm.counter);
    vm.counter += 1;
    vm.registers[r_1] = value as i8;
}

fn movr(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_2].clone();
}

fn add(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] + vm.registers[r_2]
}

fn sub(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] - vm.registers[r_2]
}

fn mul(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] * vm.registers[r_2]
}

fn div(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] / vm.registers[r_2]
}

fn inc(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] += 1
}

fn dec(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] -= 1
}

fn and(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] & vm.registers[r_2]
}

fn or(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] | vm.registers[r_2]
}

fn not(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = !vm.registers[r_1]
}

fn xor(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] ^ vm.registers[r_2]
}

fn shl(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] << 1
}

fn shr(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] >> 1
}

fn load(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let address = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.get_mem(address) as i8
}

fn store(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let address = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    vm.memory[address] = vm.registers[r_1] as u8
}

fn cmp(vm: &mut VM) {
    let r_1 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;
    let r_2 = vm.get_mem(vm.counter) as usize;
    vm.counter += 1;

    vm.cf = false;
    vm.zf = false;

    let result = vm.registers[r_1] - vm.registers[r_2];
    if result < 0 {
        vm.cf = true
    } else if result == 0 {
        vm.zf = true
    }
}

fn beq(vm: &mut VM) {
    if vm.zf {
        let address = vm.get_mem(vm.counter);
        vm.counter = address as usize;
    } else {
        vm.counter += 1;
    }
}

fn blo(vm: &mut VM) {
    if vm.cf {
        let address = vm.get_mem(vm.counter);
        vm.counter = address as usize;
    } else {
        vm.counter += 1;
    }
}

fn bhi(vm: &mut VM) {
    if !vm.cf && !vm.zf {
        let address = vm.get_mem(vm.counter);
        vm.counter = address as usize;
    } else {
        vm.counter += 1;
    }
}

fn bleq(vm: &mut VM) {
    if vm.cf || vm.zf {
        let address = vm.get_mem(vm.counter);
        vm.counter = address as usize;
    } else {
        vm.counter += 1;
    }
}

fn bheq(vm: &mut VM) {
    if !vm.cf || vm.zf {
        let address = vm.get_mem(vm.counter);
        vm.counter = address as usize;
    } else {
        vm.counter += 1;
    }
}

fn b(vm: &mut VM) {
    let address = vm.get_mem(vm.counter);
    vm.counter = address as usize;
}

fn push(vm: &mut VM) {
    if (vm.get_reg(7) as usize) & 0xFF == vm.memory.len() + 1 {
        panic!("VM close to instructions")
    }
    let value = vm.get_mem(vm.counter);
    vm.counter += 1;
    vm.memory[(vm.get_reg(7) as usize) & 0xFF] = value;
    vm.registers[7] -= 1;
}

fn pop(vm: &mut VM) {
    if (vm.get_reg(7) as usize) & 0xFF == 255 {
        panic!("VM at top of stack")
    }
    let register = vm.get_mem(vm.counter);
    vm.counter += 1;
    vm.registers[7] += 1;
    vm.registers[register as usize] = vm.get_mem((vm.get_reg(7) as usize) & 0xFF) as i8;
}

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

fn ret(vm: &mut VM) {
    if (vm.get_reg(7) as usize) & 0xFF == 255 {
        panic!("VM at top of stack")
    }
    vm.registers[7] += 1;
    vm.counter = vm.memory[(vm.get_reg(7) as usize) & 0xFF] as usize;
}

fn hlt(vm: &mut VM) {
    vm.running = false
}