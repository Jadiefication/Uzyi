package io.jadie

class VMLoader {
    companion object {
        @JvmStatic
        external fun loadCodes(opcodes: ByteArray): ByteArray
    }
}