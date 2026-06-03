package io.jadie.asm

const val SYS_CYCLES = 0xFC.toByte()
const val SYS_TIME = 0xFD.toByte()

/**
 * The [Asm] class provides a DSL for generating Uzyi bytecode.
 */
data class Asm(
    val data: MutableList<Byte>
) {

    val lRegistry = mutableMapOf<String, Int>()
    val fRegistry = mutableListOf<FixUp>()

    /**
     * MOV register, value: Loads an immediate value into a register.
     */
    fun mov(register: Byte, value: Byte) {
        data.addAll(listOf(0x00, register, value))
    }

    /**
     * MOVR fRegister, sRegister: Copies the value from sRegister to fRegister.
     */
    fun movr(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x01, fRegister, sRegister))
    }

    /**
     * ADD fRegister, sRegister: Adds sRegister to fRegister.
     */
    fun add(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x02, fRegister, sRegister))
    }

    /**
     * SUB fRegister, sRegister: Subtracts sRegister from fRegister.
     */
    fun sub(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x03, fRegister, sRegister))
    }

    /**
     * MUL fRegister, sRegister: Multiplies fRegister by sRegister.
     */
    fun mul(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x04, fRegister, sRegister))
    }

    /**
     * DIV fRegister, sRegister: Divides fRegister by sRegister.
     */
    fun div(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x05, fRegister, sRegister))
    }

    /**
     * INC register: Increments the value in the register.
     */
    infix fun inc(register: Byte) {
        data.addAll(listOf(0x06, register))
    }

    /**
     * DEC register: Decrements the value in the register.
     */
    infix fun dec(register: Byte) {
        data.addAll(listOf(0x07, register))
    }

    /**
     * AND fRegister, sRegister: Bitwise AND of fRegister and sRegister.
     */
    fun and(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x08, fRegister, sRegister))
    }

    /**
     * OR fRegister, sRegister: Bitwise OR of fRegister and sRegister.
     */
    fun or(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x09, fRegister, sRegister))
    }

    /**
     * NOT register: Bitwise NOT of the register.
     */
    infix fun not(register: Byte) {
        data.addAll(listOf(0x0A, register))
    }

    /**
     * XOR fRegister, sRegister: Bitwise XOR of fRegister and sRegister.
     */
    fun xor(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x0B, fRegister, sRegister))
    }

    /**
     * SHL register: Logical shift left of the register.
     */
    infix fun shl(register: Byte) {
        data.addAll(listOf(0x0C, register))
    }

    /**
     * SHR register: Logical shift right of the register.
     */
    infix fun shr(register: Byte) {
        data.addAll(listOf(0x0D, register))
    }

    /**
     * LOAD register, address: Loads a value from memory at [address] into [register].
     */
    fun load(register: Byte, address: Byte) {
        data.addAll(listOf(0x0E, register, address))
    }

    /**
     * STORE register, address: Stores the value of [register] into memory at [address].
     */
    fun store(register: Byte, address: Byte) {
        data.addAll(listOf(0x0F, register, address))
    }

    /**
     * CMP fRegister, sRegister: Compares fRegister and sRegister, setting flags.
     */
    fun cmp(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x10, fRegister, sRegister))
    }

    /**
     * BEQ address: Branch to [address] if ZF is set.
     */
    infix fun beq(address: Byte) {
        data.addAll(listOf(0x11, address))
    }

    /**
     * BLO address: Branch to [address] if CF is set.
     */
    infix fun blo(address: Byte) {
        data.addAll(listOf(0x12, address))
    }

    /**
     * BHI address: Branch to [address] if neither CF nor ZF are set.
     */
    infix fun bhi(address: Byte) {
        data.addAll(listOf(0x13, address))
    }

    /**
     * BLEQ address: Branch to [address] if CF or ZF is set.
     */
    infix fun bleq(address: Byte) {
        data.addAll(listOf(0x14, address))
    }

    /**
     * BHEQ address: Branch to [address] if CF is not set or ZF is set.
     */
    infix fun bheq(address: Byte) {
        data.addAll(listOf(0x15, address))
    }

    /**
     * B address: Unconditional branch to [address].
     */
    infix fun b(address: Byte) {
        data.addAll(listOf(0x16, address))
    }

    /**
     * BEQ label: Branch to [label] if ZF is set.
     */
    infix fun beq(label: String) {
        data.addAll(listOf(0x11, 0x0))
        fRegistry.add(FixUp(label, currentAddress() - 1))
    }

    /**
     * BLO label: Branch to [label] if CF is set.
     */
    infix fun blo(label: String) {
        data.addAll(listOf(0x12, 0x0))
        fRegistry.add(FixUp(label, currentAddress() - 1))
    }

    /**
     * BHI label: Branch to [label] if neither CF nor ZF are set.
     */
    infix fun bhi(label: String) {
        data.addAll(listOf(0x13, 0x0))
        fRegistry.add(FixUp(label, currentAddress() - 1))
    }

    /**
     * BLEQ label: Branch to [label] if CF or ZF is set.
     */
    infix fun bleq(label: String) {
        data.addAll(listOf(0x14, 0x0))
        fRegistry.add(FixUp(label, currentAddress() - 1))
    }

    /**
     * BHEQ label: Branch to [label] if CF is not set or ZF is set.
     */
    infix fun bheq(label: String) {
        data.addAll(listOf(0x15, 0x0))
        fRegistry.add(FixUp(label, currentAddress() - 1))
    }

    /**
     * B label: Unconditional branch to [label].
     */
    infix fun b(label: String) {
        data.addAll(listOf(0x16, 0x0))
        fRegistry.add(FixUp(label, currentAddress() - 1))
    }

    /**
     * HLT: Halts execution.
     */
    fun hlt() {
        data.add(0xFF.toByte())
    }

    /**
     * PUSH value: Pushes an immediate value onto the stack.
     */
    infix fun push(value: Byte) {
        data.addAll(listOf(0x17, value))
    }

    /**
     * POP register: Pops a value from the stack into the register.
     */
    infix fun pop(register: Byte) {
        data.addAll(listOf(0x18, register))
    }

    /**
     * CALL address: Pushes next instruction address and branches to [address].
     */
    infix fun call(address: Byte) {
        data.addAll(listOf(0x19, address))
    }

    /**
     * RET: Returns from subroutine.
     */
    fun ret() {
        data.add(0x1A)
    }

    /**
     * ADDI register, value: Adds an immediate value to the register.
     */
    fun addi(register: Byte, value: Byte) {
        data.addAll(listOf(0x1B, register, value))
    }

    /**
     * SUBI register, value: Subtracts an immediate value from the register.
     */
    fun subi(register: Byte, value: Byte) {
        data.addAll(listOf(0x1C, register, value))
    }

    /**
     * MULI register, value: Multiplies the register by an immediate value.
     */
    fun muli(register: Byte, value: Byte) {
        data.addAll(listOf(0x1D, register, value))
    }

    /**
     * Returns the current bytecode size (current address).
     */
    fun currentAddress(): Int {
        return data.size
    }

    /**
     * LOADR fRegister, sRegister: Loads from address in sRegister into fRegister.
     */
    fun loadr(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x1E, fRegister, sRegister))
    }

    /**
     * STORER fRegister, sRegister: Stores fRegister into address in sRegister.
     */
    fun storer(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x1F, fRegister, sRegister))
    }

    /**
     * Defines a [label] at the current address.
     */
    fun label(name: String) {
        lRegistry[name] = currentAddress()
    }

    fun sleep(highByte: Byte, lowByte: Byte) {
        data.addAll(listOf(0x20, highByte, lowByte))
    }
}

/**
 * Assembles a block of Uzyi instructions into a [ByteArray].
 */
fun assemble(builder: Asm.() -> Unit): ByteArray {
    val asm = Asm(mutableListOf())
    asm.builder()
    asm.fRegistry.forEach {
        val pos = asm.lRegistry[it.labelName] ?: throw RuntimeException("Missing label ${it.labelName}")
        asm.data[it.placeholderIndex] = pos.toByte()
    }
    return asm.data.toByteArray()
}