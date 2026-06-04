package io.jadie

import io.jadie.asm.assemble
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test
import kotlin.test.assertEquals
import java.io.File
import kotlin.experimental.inv

class VMTest {

    @Test
    fun testSimpleScript() {
        val opcodes = assemble {
            mov(0, 10)
            mov(1, 20)
            add(0, 1)
            hlt()
        }

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(30.toByte(), registers[0], "R0 should be 30")
        assertEquals(20.toByte(), registers[1], "R1 should be 20")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testSubtraction() {
        val opcodes = assemble {
            mov(0, 50)
            mov(1, 15)
            sub(0, 1)
            hlt()
        }

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(35.toByte(), registers[0], "R0 should be 35")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testMultiplication() {
        val opcodes = assemble {
            mov(0, 6)
            mov(1, 7)
            mul(0, 1)
            hlt()
        }

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(42.toByte(), registers[0], "R0 should be 42")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testDivision() {
        val opcodes = assemble {
            mov(0, 42)
            mov(1, 6)
            div(0, 1)
            hlt()
        }

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(7.toByte(), registers[0], "R0 should be 7 (42 / 6)")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testLoop() {
        val opcodes = assemble {
            mov(0, 0)
            mov(1, 5)

            label("start")
            cmp(0, 1)
            bheq("exit")

            inc(0)
            b("start")

            label("exit")
            hlt()
        }

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(5.toByte(), registers[0], "R0 should be 5 after loop")

        VMLoader.freeVM(vm)
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

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(0b1000.toByte(), registers[2], "AND failed")
        assertEquals(0b1110.toByte(), registers[3], "OR failed")
        assertEquals(0b0110.toByte(), registers[4], "XOR failed")
        assertEquals((0b1010.toByte().inv()), registers[5], "NOT failed")

        VMLoader.freeVM(vm)
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

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(4.toByte(), registers[0], "SHL failed")
        assertEquals(8.toByte(), registers[1], "SHR failed")

        VMLoader.freeVM(vm)
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

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(20.toByte(), registers[0], "First POP failed")
        assertEquals(10.toByte(), registers[1], "Second POP failed")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testSubroutine() {
        val opcodes = assemble {
            mov(0, 0)
            // Use label system to find subroutine instead of hardcoding address 6
            call(6)
            hlt()

            // Subroutine code
            mov(0, 42)
            ret()
        }

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(42.toByte(), registers[0], "Subroutine failed to update R0")

        VMLoader.freeVM(vm)
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

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(123.toByte(), registers[1], "Memory LOAD/STORE failed")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testConditionalBranches() {
        val opcodes = assemble {
            // --- TEST BEQ ---
            mov(0, 10)
            mov(1, 10)
            cmp(0, 1)
            beq("beq_true")

            mov(2, 1)
            hlt()

            label("beq_true")
            mov(2, 2)

            // --- TEST BLO ---
            mov(0, 5)
            mov(1, 10)
            cmp(0, 1)
            blo("blo_true")

            mov(3, 1)
            hlt()

            label("blo_true")
            mov(3, 2)

            // --- TEST BHI ---
            mov(0, 15)
            mov(1, 10)
            cmp(0, 1)
            bhi("bhi_true")

            mov(4, 1)
            hlt()

            label("bhi_true")
            mov(4, 2)

            hlt()
        }

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(2.toByte(), registers[2], "BEQ failed")
        assertEquals(2.toByte(), registers[3], "BLO failed")
        assertEquals(2.toByte(), registers[4], "BHI failed")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testImmediateArithmetic() {
        val opcodes = assemble {
            mov(0, 10)
            addi(0, 5)  // 10 + 5 = 15
            subi(0, 3)  // 15 - 3 = 12
            muli(0, 4)  // 12 * 4 = 48
            hlt()
        }

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(48.toByte(), registers[0], "Immediate arithmetic chain failed")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testRegisterMemoryOps() {
        val opcodes = assemble {
            mov(0, 50)  // Store target memory pointer in R0
            mov(1, 123) // Store payload data in R1
            storer(1, 0) // Write payload from R1 into address pointed to by R0
            mov(2, 0)   // Clear R2
            loadr(2, 0) // Read from memory address pointed to by R0 back into R2
            hlt()
        }

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(123.toByte(), registers[2], "Register-based LOAD/STORE failed")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testLabels() {
        val opcodes = assemble {
            mov(0, 0)
            b("skip")
            inc(0)
            label("skip")
            inc(0)
            hlt()
        }

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(1.toByte(), registers[0], "Branching to label failed")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testBLEQandBHEQ() {
        val opcodes = assemble {
            // Test BLEQ (<=)
            mov(0, 10)
            mov(1, 10)
            cmp(0, 1)
            bleq("passed_bleq_eq")
            mov(2, 1)
            hlt()
            label("passed_bleq_eq")
            mov(2, 2)

            mov(0, 5)
            mov(1, 10)
            cmp(0, 1)
            bleq("passed_bleq_lo")
            mov(3, 1)
            hlt()
            label("passed_bleq_lo")
            mov(3, 2)

            // Test BHEQ (>=)
            mov(0, 10)
            mov(1, 10)
            cmp(0, 1)
            bheq("passed_bheq_eq")
            mov(4, 1)
            hlt()
            label("passed_bheq_eq")
            mov(4, 2)

            mov(0, 15)
            mov(1, 10)
            cmp(0, 1)
            bheq("passed_bheq_hi")
            mov(5, 1)
            hlt()
            label("passed_bheq_hi")
            mov(5, 2)

            hlt()
        }

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(2.toByte(), registers[2], "BLEQ (Equal) failed")
        assertEquals(2.toByte(), registers[3], "BLEQ (Lower) failed")
        assertEquals(2.toByte(), registers[4], "BHEQ (Equal) failed")
        assertEquals(2.toByte(), registers[5], "BHEQ (Higher) failed")

        VMLoader.freeVM(vm)
    }
}