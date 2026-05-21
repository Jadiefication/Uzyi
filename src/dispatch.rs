use std::sync::LazyLock;
use crate::vm::{VM};

fn empty(_vm: &mut VM) {}

pub static TABLE: LazyLock<[fn(&mut VM); 256]> = LazyLock::new(|| {
    let mut table = [empty as fn(&mut VM); 256];

    table[0] = mov;
    table[1] = movr;
    table[2] = add;
    table[3] = sub;
    table[4] = mul;
    table[5] = div;
    table[6] = inc;
    table[7] = dec;

    table[8] = and;
    table[9] = or;
    table[10] = not;
    table[11] = xor;
    table[12] = shl;
    table[13] = shr;

    table[14] = load;
    table[15] = store;
    table[16] = cmp;
    table[17] = beq;
    table[18] = blo;
    table[19] = bhi;
    table[20] = bleq;
    table[21] = bheq;
    table[22] = b;

    table[255] = hlt;

    table
});

fn mov(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let value = vm.memory[vm.counter];
    vm.counter += 1;
    vm.registers[r_1] = value as i8;
}

fn movr(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let r_2 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_2].clone();
}

fn add(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let r_2 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] + vm.registers[r_2]
}

fn sub(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let r_2 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] - vm.registers[r_2]
}

fn mul(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let r_2 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] * vm.registers[r_2]
}

fn div(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let r_2 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] / vm.registers[r_2]
}

fn inc(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] += 1
}

fn dec(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] -= 1
}

fn and(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let r_2 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] & vm.registers[r_2]
}

fn or(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let r_2 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] | vm.registers[r_2]
}

fn not(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] = !vm.registers[r_1]
}

fn xor(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let r_2 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] ^ vm.registers[r_2]
}

fn shl(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] << 1
}

fn shr(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.registers[r_1] >> 1
}

fn load(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let address = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.registers[r_1] = vm.memory[address] as i8
}

fn store(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let address = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    vm.memory[address] = vm.registers[r_1] as u8
}

fn cmp(vm: &mut VM) {
    let r_1 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let r_2 = vm.memory[vm.counter] as usize;
    vm.counter += 1;
    let result = vm.registers[r_1] - vm.registers[r_2];
    if result < 0 {
        vm.cf = true
    } else if result == 0 {
        vm.zf = true
    }
}

fn beq(vm: &mut VM) {
    if vm.zf {
        let address = vm.memory[vm.counter];
        vm.counter = address as usize;
    }
}

fn blo(vm: &mut VM) {
    if vm.cf {
        let address = vm.memory[vm.counter];
        vm.counter = address as usize;
    }
}

fn bhi(vm: &mut VM) {
    if !vm.cf && !vm.zf {
        let address = vm.memory[vm.counter];
        vm.counter = address as usize;
    }
}

fn bleq(vm: &mut VM) {
    if vm.cf || vm.zf {
        let address = vm.memory[vm.counter];
        vm.counter = address as usize;
    }
}

fn bheq(vm: &mut VM) {
    if !vm.cf || vm.zf {
        let address = vm.memory[vm.counter];
        vm.counter = address as usize;
    }
}

fn b(vm: &mut VM) {
    let address = vm.memory[vm.counter];
    vm.counter = address as usize;
}

fn hlt(vm: &mut VM) {
    vm.running = false
}