# Uzyi Examples

This directory contains example programs written for the Uzyi Virtual Machine using the Kotlin DSL Assembler.

## Examples Overview

### [Array Sum](array_sum.uzyi)
Demonstrates memory operations and looping. It initializes an array in memory and calculates the sum of its elements.
- **Concepts**: `store`, `loadr`, `add`, `inc`, `dec`, `cmp`, `beq`, `b`.

### [Factorial](factorial.uzyi)
Calculates the factorial of a number (n!) using a loop.
- **Concepts**: `mov`, `mul`, `dec`, `cmp`, `beq`, `label`.

### [Fibonacci](fibonacci.uzyi)
Computes the n-th Fibonacci number.
- **Concepts**: `movr`, `add`, `dec`, `cmp`, `beq`, `b`.

## How to Run

These examples are automatically picked up and verified by the test suite. To run them, execute the following from the `web` directory:

```bash
./gradlew test --tests "io.jadie.examples.ExamplesTest"
```

## Writing Your Own

Uzyi programs are written using a Kotlin DSL. The `.uzyi` files in this directory use a simplified syntax that is parsed and executed by the `ExamplesTest`.

To run these files in your own environment:
1.  **Setup a Kotlin Script (KTS) Engine**: Use the `ScriptEngineManager` to get a "kts" engine.
2.  **Pass File Contents**: Read the `.uzyi` file and pass its content into an `assemble { ... }` block.
3.  **Assemble to Opcodes**: The `assemble` block executes the DSL instructions and constructs the corresponding opcode values (as a `ByteArray`), which can then be loaded into the VM.

Example of how the `ExamplesTest` handles this:
```kotlin
val script = file.readText()
val engine = ScriptEngineManager().getEngineByExtension("kts")!!
val opcodes = engine.eval("import io.jadie.asm.*; assemble { $script }") as ByteArray
```

A typical program structure involves:
1.  **Initialization**: Setting up registers and memory.
2.  **Main Logic**: The core algorithm, often using `label` for control flow.
3.  **Termination**: Using `hlt()` to stop the VM.
