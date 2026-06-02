package io.jadie

import io.jadie.asm.assemble
import kotlin.test.Test
import kotlin.test.assertEquals
import java.io.File
import kotlin.experimental.inv

class VMTest {

    init {
        val libPath = File("../target/debug/libCustom_ISA.dylib").absolutePath
        System.load(libPath)
    }

    @Test
    fun testSimpleScript() {
        val opcodes = assemble {
            mov(0, 10)
            mov(1, 20)
            add(0, 1)
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
            sub(0, 1)
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
            mul(0, 1)
            hlt()
        }

        val registers = VMLoader.loadCodes(opcodes)
        assertEquals(42.toByte(), registers[0], "R0 should be 42")
    }

    @Test
    fun testLoop() {
        val opcodes = assemble {
            mov(0, 0)
            mov(1, 5)

            cmp(0, 1)
            bheq(15)

            inc(0)
            b(6)

            hlt()
        }

        val registers = VMLoader.loadCodes(opcodes)
        assertEquals(5.toByte(), registers[0], "R0 should be 5 after loop")
    }

    @Test
    fun testBitwiseOps() {
        val opcodes = assemble {
            mov(0, 0b1010)
            mov(1, 0b1100)

            movr(2, 0)
            and(2, 1)

            movr(3, 0)
            or(3, 1)

            movr(4, 0)
            xor(4, 1)

            movr(5, 0)
            not(5)

            hlt()
        }

        val registers = VMLoader.loadCodes(opcodes)
        assertEquals(0b1000.toByte(), registers[2], "AND failed")
        assertEquals(0b1110.toByte(), registers[3], "OR failed")
        assertEquals(0b0110.toByte(), registers[4], "XOR failed")
        assertEquals((0b1010.toByte().inv()), registers[5], "NOT failed")
    }

    @Test
    fun testShifts() {
        val opcodes = assemble {
            mov(0, 1)
            shl(0)
            shl(0)

            mov(1, 16)
            shr(1)

            hlt()
        }

        val registers = VMLoader.loadCodes(opcodes)
        assertEquals(4.toByte(), registers[0], "SHL failed")
        assertEquals(8.toByte(), registers[1], "SHR failed")
    }

    @Test
    fun testStackOps() {
        val opcodes = assemble {
            push(10)
            push(20)
            pop(0)
            pop(1)
            hlt()
        }

        val registers = VMLoader.loadCodes(opcodes)
        assertEquals(20.toByte(), registers[0], "First POP failed")
        assertEquals(10.toByte(), registers[1], "Second POP failed")
    }

    @Test
    fun testSubroutine() {
        val opcodes = assemble {
            mov(0, 0)
            call(6)
            hlt()

            mov(0, 42)
            ret()
        }

        val registers = VMLoader.loadCodes(opcodes)
        assertEquals(42.toByte(), registers[0], "Subroutine failed to update R0")
    }

    @Test
    fun testMemoryOps() {
        val opcodes = assemble {
            mov(0, 123)
            store(0, 100)
            mov(1, 0)
            load(1, 100)
            hlt()
        }

        val registers = VMLoader.loadCodes(opcodes)
        assertEquals(123.toByte(), registers[1], "Memory LOAD/STORE failed")
    }

    @Test
    fun testConditionalBranches() {
        val opcodes = assemble {
            // --- TEST BEQ ---
            mov(0, 10)
            mov(1, 10)
            cmp(0, 1)

            // Current address + 2 (beq size) + 3 (mov size) + 1 (hlt size) = 6
            val beqTarget = currentAddress() + 6
            beq(beqTarget.toByte())

            mov(2, 1)
            hlt()

            // True branch execution point
            mov(2, 2)

            val bloStart = currentAddress() + 2
            b(bloStart.toByte())

            // --- TEST BLO ---
            mov(0, 5)
            mov(1, 10)
            cmp(0, 1)

            val bloTarget = currentAddress() + 6
            blo(bloTarget.toByte())

            mov(3, 1)
            hlt()

            // True branch execution point
            mov(3, 2)

            val bhiStart = currentAddress() + 2
            b(bhiStart.toByte())

            // --- TEST BHI ---
            mov(0, 15)
            mov(1, 10)
            cmp(0, 1)

            val bhiTarget = currentAddress() + 6
            bhi(bhiTarget.toByte())

            mov(4, 1)
            hlt()

            // True branch execution point
            mov(4, 2)

            hlt()
        }

        val registers = VMLoader.loadCodes(opcodes)
        assertEquals(2.toByte(), registers[2], "BEQ failed")
        assertEquals(2.toByte(), registers[3], "BLO failed")
        assertEquals(2.toByte(), registers[4], "BHI failed")
    }
}