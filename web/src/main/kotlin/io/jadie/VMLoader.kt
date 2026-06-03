package io.jadie

class VMLoader {
    companion object {
        @JvmStatic
        external fun loadCodes(opcodes: ByteArray): VMState

        @JvmStatic
        external fun createVM(opcodes: ByteArray): Long

        @JvmStatic
        external fun stepVM(vmPointer: Long): VMState

        @JvmStatic
        external fun freeVM(vmPointer: Long)
    }
}