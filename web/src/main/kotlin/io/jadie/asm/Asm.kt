package src.main.kotlin.io.jadie.asm

data class Asm(
    val data: MutableList<Byte>
) {
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

    fun hlt() {
        data.add(0xFF.toByte())
    }
}

fun assemble(builder: Asm.() -> Unit): ByteArray {
    val asm = Asm(mutableListOf())
    asm.builder()
    return asm.data.toByteArray()
}