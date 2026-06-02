package io.jadie.asm

data class Asm(
    val data: MutableList<Byte>
) {

    val lRegistry = mutableMapOf<String, Int>()
    val fRegistry = mutableListOf<FixUp>()

    fun mov(register: Byte, value: Byte) {
        data.addAll(listOf(0x00, register, value))
    }

    fun movr(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x01, fRegister, sRegister))
    }

    fun add(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x02, fRegister, sRegister))
    }

    fun sub(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x03, fRegister, sRegister))
    }

    fun mul(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x04, fRegister, sRegister))
    }

    fun div(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x05, fRegister, sRegister))
    }

    infix fun inc(register: Byte) {
        data.addAll(listOf(0x06, register))
    }

    infix fun dec(register: Byte) {
        data.addAll(listOf(0x07, register))
    }

    fun and(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x08, fRegister, sRegister))
    }

    fun or(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x09, fRegister, sRegister))
    }

    infix fun not(register: Byte) {
        data.addAll(listOf(0x0A, register))
    }

    fun xor(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x0B, fRegister, sRegister))
    }

    infix fun shl(register: Byte) {
        data.addAll(listOf(0x0C, register))
    }

    infix fun shr(register: Byte) {
        data.addAll(listOf(0x0D, register))
    }

    fun load(register: Byte, address: Byte) {
        data.addAll(listOf(0x0E, register, address))
    }

    fun store(register: Byte, address: Byte) {
        data.addAll(listOf(0x0F, register, address))
    }

    fun cmp(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x10, fRegister, sRegister))
    }

    infix fun beq(address: Byte) {
        data.addAll(listOf(0x11, address))
    }

    infix fun blo(address: Byte) {
        data.addAll(listOf(0x12, address))
    }

    infix fun bhi(address: Byte) {
        data.addAll(listOf(0x13, address))
    }

    infix fun bleq(address: Byte) {
        data.addAll(listOf(0x14, address))
    }

    infix fun bheq(address: Byte) {
        data.addAll(listOf(0x15, address))
    }

    infix fun b(address: Byte) {
        data.addAll(listOf(0x16, address))
    }

    infix fun beq(label: String) {
        data.addAll(listOf(0x11, 0x0))
        fRegistry.add(FixUp(label, currentAddress() - 1))
    }

    infix fun blo(label: String) {
        data.addAll(listOf(0x12, 0x0))
        fRegistry.add(FixUp(label, currentAddress() - 1))
    }

    infix fun bhi(label: String) {
        data.addAll(listOf(0x13, 0x0))
        fRegistry.add(FixUp(label, currentAddress() - 1))
    }

    infix fun bleq(label: String) {
        data.addAll(listOf(0x14, 0x0))
        fRegistry.add(FixUp(label, currentAddress() - 1))
    }

    infix fun bheq(label: String) {
        data.addAll(listOf(0x15, 0x0))
        fRegistry.add(FixUp(label, currentAddress() - 1))
    }

    infix fun b(label: String) {
        data.addAll(listOf(0x16, 0x0))
        fRegistry.add(FixUp(label, currentAddress() - 1))
    }

    fun hlt() {
        data.add(0xFF.toByte())
    }

    infix fun push(value: Byte) {
        data.addAll(listOf(0x17, value))
    }

    infix fun pop(register: Byte) {
        data.addAll(listOf(0x18, register))
    }

    infix fun call(address: Byte) {
        data.addAll(listOf(0x19, address))
    }

    fun ret() {
        data.add(0x1A)
    }

    fun addi(register: Byte, value: Byte) {
        data.addAll(listOf(0x1B, register, value))
    }

    fun subi(register: Byte, value: Byte) {
        data.addAll(listOf(0x1C, register, value))
    }

    fun muli(register: Byte, value: Byte) {
        data.addAll(listOf(0x1D, register, value))
    }

    fun currentAddress(): Int {
        return data.size
    }

    fun loadr(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x1E, fRegister, sRegister))
    }

    fun storer(fRegister: Byte, sRegister: Byte) {
        data.addAll(listOf(0x1F, fRegister, sRegister))
    }

    fun label(name: String) {
        lRegistry[name] = currentAddress()
    }
}

fun assemble(builder: Asm.() -> Unit): ByteArray {
    val asm = Asm(mutableListOf())
    asm.builder()
    asm.fRegistry.forEach {
        val pos = asm.lRegistry[it.labelName] ?: throw RuntimeException("Missing label ${it.labelName}")
        asm.data[it.placeholderIndex] = pos.toByte()
    }
    return asm.data.toByteArray()
}