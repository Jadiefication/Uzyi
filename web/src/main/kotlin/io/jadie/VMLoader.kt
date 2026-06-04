package io.jadie

import java.nio.file.Files

class VMLoader {
    companion object {
        init {
            loadNativeLibrary()
        }

        private fun loadNativeLibrary() {
            val os = System.getProperty("os.name").lowercase()
            val arch = System.getProperty("os.arch").lowercase()
            
            val suffix = when {
                os.contains("win") -> ".dll"
                os.contains("mac") -> ".dylib"
                else -> ".so"
            }
            
            val prefix = if (os.contains("win")) "" else "lib"
            val libName = "${prefix}Uzyi$suffix"
            val resourcePath = "/native/$libName"

            val inputStream = VMLoader::class.java.getResourceAsStream(resourcePath) ?: try {
                System.loadLibrary("Uzyi")
                return
            } catch (e: UnsatisfiedLinkError) {
                throw RuntimeException("Could not find native library $libName in resources or library path")
            }

            val tempFile = Files.createTempFile("libuzyi", suffix).toFile()
            tempFile.deleteOnExit()
            
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            System.load(tempFile.absolutePath)
        }

        @JvmStatic
        external fun runVM(vmPointer: Long): VMState

        @JvmStatic
        external fun getState(vmPointer: Long): VMState

        @JvmStatic
        external fun createVM(opcodes: ByteArray): Long

        @JvmStatic
        external fun stepVM(vmPointer: Long): VMState

        @JvmStatic
        external fun freeVM(vmPointer: Long)
    }
}