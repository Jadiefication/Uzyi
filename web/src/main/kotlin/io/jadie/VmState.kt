package io.jadie

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
