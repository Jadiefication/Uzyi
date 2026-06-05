package io.jadie

import java.nio.file.Files

/**
 * JNI bridge and loader for the Uzyi virtual machine native library.
 *
 * Responsibilities:
 * - Locate and load the platform-specific native library from `resources/native` or system paths.
 * - Expose native entry points used by the Kotlin test suite and higher-level code.
 *
 * This class attempts to load the bundled library first. If not found (e.g., during development),
 * it falls back to `System.loadLibrary("Uzyi")` to use any library discoverable via `java.library.path`.
 */
class VMLoader {
    companion object {
        init {
            loadNativeLibrary()
        }

        /**
         * Loads the `Uzyi` native library for the current OS.
         *
         * Search order:
         * 1) Bundled in `resources/native` of this module (preferred for published artifacts)
         * 2) Fallback to `System.loadLibrary("Uzyi")` for developer environments
         *
         * Throws a [RuntimeException] if the library cannot be found or loaded.
         */
        private fun loadNativeLibrary() {
            val os = System.getProperty("os.name").lowercase()
            val arch = System.getProperty("os.arch").lowercase()

            val suffix =
                when {
                    os.contains("win") -> ".dll"
                    os.contains("mac") -> ".dylib"
                    else -> ".so"
                }

            val prefix = if (os.contains("win")) "" else "lib"
            val libName = "${prefix}Uzyi$suffix"
            val resourcePath = "/native/$libName"

            val inputStream =
                VMLoader::class.java.getResourceAsStream(resourcePath) ?: try {
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

        /**
         * Runs the VM until it halts or sleeps and returns the latest [VMState].
         * @param vmPointer Native pointer returned by [createVM].
         */
        @JvmStatic
        external fun runVM(vmPointer: Long): VMState

        /**
         * Returns a snapshot of the current [VMState] without advancing execution.
         * @param vmPointer Native pointer returned by [createVM].
         */
        @JvmStatic
        external fun getState(vmPointer: Long): VMState

        /**
         * Creates a new VM instance with the provided bytecode loaded into memory.
         * @param opcodes Assembled Uzyi bytecode (truncated to VM memory size if necessary).
         * @return A native pointer that must later be freed via [freeVM].
         */
        @JvmStatic
        external fun createVM(opcodes: ByteArray): Long

        /**
         * Executes a single instruction if possible and returns the resulting [VMState].
         * @param vmPointer Native pointer returned by [createVM].
         */
        @JvmStatic
        external fun stepVM(vmPointer: Long): VMState

        /**
         * Frees the native VM instance created by [createVM]. Safe to call with an already-freed pointer (no-op).
         * @param vmPointer Native pointer to the VM instance.
         */
        @JvmStatic
        external fun freeVM(vmPointer: Long)
    }
}
