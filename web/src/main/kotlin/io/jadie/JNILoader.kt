package src.main.kotlin.io.jadie

class JNILoader {
    companion object {
        init {
            System.loadLibrary("VM_Shit")
        }

        @JvmStatic
        external fun loadCodes(opcodes: ByteArray)
    }
}