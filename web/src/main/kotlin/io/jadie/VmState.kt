package io.jadie

import kotlinx.serialization.Serializable

@Serializable
/**
 * Immutable snapshot of the VM at a given point in time.
 *
 * - [registers]: 8 general-purpose 8-bit registers (R0..R7), where R7 is the stack pointer.
 * - [counter]: Program counter (0..255), next instruction address.
 * - [status]: Encoded execution status (implementation-specific values from native side).
 * - [cf]: Carry/borrow flag from the last comparison/arithmetic.
 * - [zf]: Zero flag from the last comparison/arithmetic.
 * - [cycles]: Total executed instruction count (exposed through SYS address 0xFC as well).
 * - [memory]: Full 256-byte memory image as signed-int array for easier interop/serialization.
 */
data class VMState(
    val registers: ByteArray,
    val counter: Int,
    val status: Int,
    val cf: Boolean,
    val zf: Boolean,
    val cycles: Int,
    val memory: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VMState

        if (counter != other.counter) return false
        if (status != other.status) return false
        if (cf != other.cf) return false
        if (zf != other.zf) return false
        if (cycles != other.cycles) return false
        if (!registers.contentEquals(other.registers)) return false
        if (!memory.contentEquals(other.memory)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = counter
        result = 31 * result + status
        result = 31 * result + cf.hashCode()
        result = 31 * result + zf.hashCode()
        result = 31 * result + cycles.hashCode()
        result = 31 * result + registers.contentHashCode()
        result = 31 * result + memory.contentHashCode()
        return result
    }
}
