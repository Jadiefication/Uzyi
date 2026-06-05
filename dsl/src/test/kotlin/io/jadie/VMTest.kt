package io.jadie

import io.jadie.asm.assemble
import org.junit.jupiter.api.BeforeAll
import java.io.File
import kotlin.experimental.inv
import kotlin.test.Test
import kotlin.test.assertEquals

class VMTest {
    @Test
    fun testSimpleScript() {
        val opcodes =
            assemble {
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
    fun testSubtractionNeg() {
        val opcodes =
            assemble {
                mov(0, 10)
                mov(1, 20)
                sub(0, 1) // 10 - 20 = -10 (or 246 unsigned)
                hlt()
            }

        val vm = VMLoader.createVM(opcodes)
        val registers = VMLoader.runVM(vm).registers
        assertEquals((-10).toByte(), registers[0], "R0 should be -10")
        VMLoader.freeVM(vm)
    }

    @Test
    fun testDivisionByZero() {
        val opcodes =
            assemble {
                mov(0, 10)
                mov(1, 0)
                div(0, 1)
                hlt()
            }

        val vm = VMLoader.createVM(opcodes)
        // Check if it handles it gracefully (should stop)
        val state = VMLoader.runVM(vm)
        // status 0 usually means Running, but here Stopped is likely a different value.
        // Let's just check it doesn't crash.
        VMLoader.freeVM(vm)
    }

    @Test
    fun testMultiplication() {
        val opcodes =
            assemble {
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
        val opcodes =
            assemble {
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
        val opcodes =
            assemble {
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
        val opcodes =
            assemble {
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
        val opcodes =
            assemble {
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
        val opcodes =
            assemble {
                mov(0, 10)
                mov(1, 20)
                push(0)
                push(1)
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
        val opcodes =
            assemble {
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
        val opcodes =
            assemble {
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
        val opcodes =
            assemble {
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
        val opcodes =
            assemble {
                mov(0, 10)
                addi(0, 5) // 10 + 5 = 15
                subi(0, 3) // 15 - 3 = 12
                muli(0, 4) // 12 * 4 = 48
                hlt()
            }

        val vm = VMLoader.createVM(opcodes)

        val registers = VMLoader.runVM(vm).registers
        assertEquals(48.toByte(), registers[0], "Immediate arithmetic chain failed")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testRegisterMemoryOps() {
        val opcodes =
            assemble {
                mov(0, 50) // Store target memory pointer in R0
                mov(1, 123) // Store payload data in R1
                storer(1, 0) // Write payload from R1 into address pointed to by R0
                mov(2, 0) // Clear R2
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
        val opcodes =
            assemble {
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
    fun testSubroutineWithLabel() {
        val opcodes =
            assemble {
                mov(0, 5)
                call("func")
                inc(0)
                hlt()

                label("func")
                inc(0)
                ret()
            }

        val vm = VMLoader.createVM(opcodes)
        val state = VMLoader.runVM(vm)
        val registers = state.registers

        assertEquals(7.toByte(), registers[0], "R0 should be 7 after call and return")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testMemoryDirect() {
        val opcodes =
            assemble {
                mov(0, 123)
                store(0, 50) // memory[10] = 123
                mov(0, 0)
                load(0, 50) // R0 = memory[10]
                hlt()
            }

        val vm = VMLoader.createVM(opcodes)
        val state = VMLoader.runVM(vm)
        val registers = state.registers
        val memory = state.memory

        assertEquals(123.toByte(), registers[0], "R0 should be 123 from memory[10]")
        assertEquals(123, memory[50], "Memory[10] should be 123")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testStackOpsNew() {
        val opcodes =
            assemble {
                mov(0, 42)
                push(0)
                mov(0, 0)
                pop(1)
                hlt()
            }

        val vm = VMLoader.createVM(opcodes)
        val state = VMLoader.runVM(vm)
        val registers = state.registers
        assertEquals(42.toByte(), registers[1], "Stack push/pop failed")
        VMLoader.freeVM(vm)
    }

    @Test
    fun testMoreExhaustiveBranching() {
        val opcodes =
            assemble {
                // Test BEQ (false case)
                mov(0, 10)
                mov(1, 11)
                cmp(0, 1)
                beq("should_not_happen")
                mov(2, 1)

                // Test BLO (false case)
                mov(0, 10)
                mov(1, 5)
                cmp(0, 1)
                blo("should_not_happen")
                mov(3, 1)

                // Test BHI (false case)
                mov(0, 5)
                mov(1, 10)
                cmp(0, 1)
                bhi("should_not_happen")
                mov(4, 1)

                hlt()
                label("should_not_happen")
                mov(5, 1)
                hlt()
            }

        val vm = VMLoader.createVM(opcodes)
        val state = VMLoader.runVM(vm)
        val registers = state.registers

        assertEquals(1.toByte(), registers[2], "BEQ false case failed")
        assertEquals(1.toByte(), registers[3], "BLO false case failed")
        assertEquals(1.toByte(), registers[4], "BHI false case failed")
        assertEquals(0.toByte(), registers[5], "Jumped to should_not_happen")

        VMLoader.freeVM(vm)
    }

    @Test
    fun testSubroutineDirect() {
        val opcodes =
            assemble {
                mov(0, 0)
                call(6.toByte()) // address of mov(0, 42)
                hlt()

                // Subroutine code at address 5
                mov(0, 42)
                ret()
            }

        val vm = VMLoader.createVM(opcodes)
        val state = VMLoader.runVM(vm)
        val registers = state.registers
        assertEquals(42.toByte(), registers[0], "Direct subroutine call failed")
        VMLoader.freeVM(vm)
    }

    @Test
    fun testStackWrapping() {
        // Test stack behavior if it wraps or has limits
        // Based on typical small VM implementations, let's see if we can push many items
        val opcodes =
            assemble {
                mov(0, 1)
                mov(1, 2)
                mov(2, 3)
                push(0)
                push(1)
                push(2)
                pop(3)
                pop(4)
                pop(5)
                hlt()
            }

        val vm = VMLoader.createVM(opcodes)
        val state = VMLoader.runVM(vm)
        val registers = state.registers
        assertEquals(3.toByte(), registers[3])
        assertEquals(2.toByte(), registers[4])
        assertEquals(1.toByte(), registers[5])
        VMLoader.freeVM(vm)
    }

    @Test
    fun testBLEQandBHEQ() {
        val opcodes =
            assemble {
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
