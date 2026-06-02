package src.main.kotlin.io.jadie

class VMLoader {
    companion object {
        init {
            System.loadLibrary("VM_Shit")
        }

        @JvmStatic
        external fun loadCodes(opcodes: ByteArray)
    }
}