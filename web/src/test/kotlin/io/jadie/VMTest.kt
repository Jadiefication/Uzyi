package io.jadie

import io.jadie.asm.assemble
import kotlin.test.Test
import kotlin.test.assertEquals
import java.io.File

class VMTest {

    init {
        // Force load the library from the target directory
        val libPath = File("../target/debug/libCustom_ISA.dylib").absolutePath
        System.load(libPath)
    }

    @Test
    fun testSimpleScript() {
        val opcodes = assemble {
            mov(0, 10)  // R0 = 10
            mov(1, 20)  // R1 = 20
            add(0, 1)   // R0 = R0 + R1 = 30
            hlt()
        }

        val registers = VMLoader.loadCodes(opcodes)

        assertEquals(30.toByte(), registers[0], "R0 should be 30")
        assertEquals(20.toByte(), registers[1], "R1 should be 20")
    }

    @Test
    fun testSubtraction() {
        val opcodes = assemble {
            mov(0, 50)
            mov(1, 15)
            sub(0, 1) // R0 = 50 - 15 = 35
            hlt()
        }

        val registers = VMLoader.loadCodes(opcodes)
        assertEquals(35.toByte(), registers[0], "R0 should be 35")
    }

    @Test
    fun testMultiplication() {
        val opcodes = assemble {
            mov(0, 6)
            mov(1, 7)
            mul(0, 1) // R0 = 6 * 7 = 42
            hlt()
        }

        val registers = VMLoader.loadCodes(opcodes)
        assertEquals(42.toByte(), registers[0], "R0 should be 42")
    }

    @Test
    fun testLoop() {
        // Simple loop: R0 = 0, R1 = 5; while R0 < R1: R0++
        val opcodes = assemble {
            mov(0, 0)     // [0]  R0 = 0
            mov(1, 5)     // [? ] R1 = 5 (Let's assume loop starts at address 6)
    
            // --- LOOP START (Address 6) ---
            cmp(0, 1)     // [6]  Compare R0 and R1
            bheq(15)      // [? ] Branch if Higher or Equal to HLT (Exit loop)
    
            inc(0)        // [? ] R0++ (The loop body)
            b(6)          // [? ] Jump back to CMP
    
            hlt()         // [14] Loop exit point
        }


        val registers = VMLoader.loadCodes(opcodes)
        assertEquals(5.toByte(), registers[0], "R0 should be 5 after loop")
    }
}
