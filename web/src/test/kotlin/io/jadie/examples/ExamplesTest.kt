package io.jadie.examples

import io.jadie.VMLoader
import io.jadie.VMState
import io.jadie.asm.assemble
import org.junit.jupiter.api.Test
import javax.script.ScriptEngineManager
import java.io.File
import kotlin.test.assertEquals

class ExamplesTest {

    private fun runExample(fileName: String): VMState {
        val file = File("src/examples/$fileName")
        val script = file.readText()

        val engine = ScriptEngineManager().getEngineByExtension("kts")!!

        val opcodes = engine.eval("import io.jadie.asm.*" +
                "assemble { $script }") as ByteArray

        val vm = VMLoader.createVM(opcodes)
        val state = VMLoader.runVM(vm)
        VMLoader.freeVM(vm)
        return state
    }

    @Test
    fun testFactorial() {
        val state = runExample("factorial.uzyi")
        assertEquals(120.toByte(), state.registers[1], "5! should be 120")
    }

    @Test
    fun testFibonacci() {
        val state = runExample("fibonacci.uzyi")
        assertEquals(13.toByte(), state.registers[1], "7th Fibonacci should be 13")
    }

    @Test
    fun testArraySum() {
        val state = runExample("array_sum.uzyi")
        assertEquals(150.toByte(), state.registers[2], "Sum of 10,20,30,40,50 should be 150")
    }
}
