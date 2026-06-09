<div align="center">

  <img alt="Uzyi logo" src=".github/logo.svg" width="160" height="160" />
  <h1>Uzyi</h1>
  <p>A custom emulation machine based on a custom instruction set(ISA) with a Rust backend</p>

  <p>
    <a href="https://crates.io/crates/uzyi"><img alt="Crates.io" src="https://img.shields.io/crates/v/uzyi.svg"></a>
    <a href="https://rust-lang.org"><img alt="Rust" src="https://img.shields.io/badge/rust-2024-blue.svg?logo=rust"></a>
    <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-blue.svg"></a>
  </p>
</div>

Uzyi is a small embeddable emulation library which can be used in almost any JVM environment, as long as rust works on the platform. It provides:

- **Custom ISA**: An easy-to-use ISA with a small amount of instructions.
- **Emulation Machine**: Fast execution of the loaded instructions.
- **JNI Integration**: Interop with the emulator through external Kotlin methods.
- **Kotlin Assembler**: A DSL-based assembler collapsing to a list of opcodes.

Quick links

- Security policy: [SECURITY.md](SECURITY.md)
- Contributing guide: [CONTRIBUTING.md](CONTRIBUTING.md)
- Code of Conduct: [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
- Support: [SUPPORT.md](SUPPORT.md)
- License: MIT ([LICENSE](LICENSE))
- Repository: [GitHub](https://github.com/Jadiefication/Uzyi)

## About the Project

Uzyi is a custom 8-bit emulation machine with its own ISA embeddable in high performance critical applications. It offers a Rust backend for executing the opcodes and a type-safe DSL on the Kotlin side.

### Why Uzyi?
- **Hybrid Architecture:** Offers low-level speeds with the developer experience of writing Kotlin.
- **Cross-Platform:** Compiles to many different platforms supported both by the JVM and Rust.
- **Embedded by Design:** Is small by design therefore easily embeddable in almost any application.

### Limitations
Through all that it claims to offer, Uzyi is a hobby project that has these limitations:
- **8-bit Integers:** Registers can only handle i8 integers (-128 to 127).
- **Small Memory Space:** 256 bytes of memory.
- **Limited Register File:** 8 overall registers (R0-R7), with R7 being the Stack Pointer.
- **No Floating Point:** Arithmetic calculations only account for integers.
- **Non-Standard Bytecode:** No real-life hardware can actually run this ISA without using Uzyi.

## Demo & Real-World Proof

A traditional Demo is not possible for Uzyi because:
1. It works based on **JNI (Java Native Interface)**, which loads native libraries (.so, .dll, .dylib) and interacts with them.
2. The project runs in Rust, not in a browser supported environment like WASM (though that is a potential future direction).

To try out Uzyi, you can:
- **Check the CI/CD Pipeline:** Our [JitPack integration](https://jitpack.io/#Jadiefication/Uzyi) offers the actual library, with it then compiling to your specific environment.
- **Run the Tests:** The comprehensive test suite in `dsl/src/test/kotlin/io/jadie/VMTest.kt` showcases how the emulator runs and that it works perfectly.
- **Look at the Code:** The `dsl` module showcases how the DSL is actually written and used.

## Getting Started

If you want to run Uzyi to try out the ISA:

### Prerequisites
- [Rust 1.80+](https://rustup.rs/)
- [JDK 17+](https://adoptium.net/)
- [Gradle](https://gradle.org/install/) (optional, uses wrapper)

### Local Setup & Build

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Jadiefication/Uzyi.git
   cd Uzyi
   ```

2. **Build the native library for Kotlin:**
   ```bash
   cd dsl
   ./gradlew :buildNative
   ```

3. **Run the Kotlin environment:**
   Try to run the test to ensure everything works as expected.
   ```bash
   ./gradlew test
   ```

4. **Using it in your own project:**
   You can use Uzyi as a dependency through JitPack. Add this to your `build.gradle.kts`:
   ```kotlin
   repositories {
       maven { url = uri("https://jitpack.io") }
   }
   dependencies {
       implementation("com.github.Jadiefication:Uzyi:Tag")
   }
   ```

## Code Examples & Programs

To see runnable example you can check out the `src/examples/` directory. These files showcase how different mathematical phenomenons can be calculated in our ISA:

- [**array_sum.uzyi**](dsl/src/examples/array_sum.uzyi): Defines an array in memory and populates it with numbers, it then iterates through the numbers and sums them up.
- [**factorial.uzyi**](dsl/src/examples/factorial.uzyi): Showcases a basic loop counter with arithmetic methods to calculate the factorials like `5!`.
- [**fibonacci.uzyi**](dsl/src/examples/fibonacci.uzyi): Focused on tracking the current state and shifting variable values to compute the $n$-th Fibonacci number.

You can use these examples both as documentation or motivation to experiment with the ISA for yourself to test out it's capabilities.

## ISA Overview

The Uzyi ISA includes 32+ instructions:
- **Data Movement:** `mov`, `movr`, `push`, `pop`
- **Arithmetic:** `add`, `sub`, `mul`, `div`, `inc`, `dec`, `addi`, `subi`, `muli`
- **Logical:** `and`, `or`, `xor`, `not`, `shl`, `shr`
- **Control Flow:** `cmp`, `beq`, `blo`, `bhi`, `bleq`, `bheq`, `b`, `call`, `ret`
- **Memory:** `load`, `store`, `loadr`, `storer`
- **System:** `hlt`, `sleep`

### Registers
- **R0 - R6:** General-purpose 8-bit registers.
- **R7:** Stack Pointer (SP). It's value at start up is set to 255 (top of memory).

### Memory Map
- **0x00 - 0xFB:** General-purpose RAM (Instructions start at 0x00).
- **0xFC:** Cycle counter (Read-only).
- **0xFD:** Execution timer (Read-only).
- **0xFE - 0xFF:** Reserved.

## Commands & Scripts

The project uses Cargo and Gradle as it's build tools:

- `cargo build`: Build the Rust emulator.
- `./gradlew build`: Build the Kotlin assembler (in `dsl/` directory).
- `cargo fmt`: Format the codebase.

## Principles

#### Unopinionated
Uzyi doesn't want to force you into anything, it just simply provides an emulator you can use to your liking.

#### Performance
Uses Rust as it's language of choice for the backend to ensure safety and speed.

#### Testable
Everything is designed to be testable, to make sure everything works and doesn't break at runtime.

## Documentation

Starting points for learning about the code structure:

- `uzyi::vm::VM` — The main emulator state.
- `uzyi::instructions::Instruction` — ISA definitions.
- `io.jadie.asm.Asm` — Kotlin Assembler DSL.

## Testing

The test suit focuses on full coverage whilst maintaining easy-of-use.
- Run Kotlin tests: `./gradlew test`

## Contributing

Contributions are always welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

## License

[MIT](LICENSE) — © 2026 Jadiefication
