plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
    `maven-publish`
}

group = "io.jadie"
version = "0.1.5"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    testImplementation("org.jetbrains.kotlin:kotlin-scripting-jsr223:2.3.21")
}

kotlin {
    jvmToolchain(25)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform()
}

val nativeResourceDir = layout.projectDirectory.dir("src/main/resources/native")

val buildNative by tasks.registering {
    description = "Build the Rust native library and copy it into resources/native."
    doLast {
        val isJitpack = System.getenv("JITPACK") == "true"
        val os = System.getProperty("os.name").lowercase()

        // Match only the native Linux target on JitPack.
        val targets = if (isJitpack && os.contains("linux")) {
            listOf("x86_64-unknown-linux-gnu")
        } else {
            listOf("") // Standard local compilation
        }

        fun runCommand(vararg cmd: String) {
            val pb = ProcessBuilder(*cmd)
            pb.directory(File(".."))
            pb.redirectErrorStream(true)
            pb.inheritIO()
            val p = pb.start()
            val code = p.waitFor()
            if (code != 0) throw GradleException("Command failed: ${cmd.joinToString(" ")} (exit $code)")
        }

        // Setup the target array cleanly
        if (isJitpack && os.contains("linux")) {
            try {
                runCommand("rustup", "target", "add", "x86_64-unknown-linux-gnu")
            } catch (e: Exception) {
                logger.warn("Failed to add Rust target: ${e.message}")
            }
        }

        targets.forEach { target ->
            val targetArgs = mutableListOf("cargo", "build", "--release")
            if (target.isNotEmpty()) {
                targetArgs.add("--target")
                targetArgs.add(target)
            }

            runCommand(*targetArgs.toTypedArray())

            val (prefix, suffix) = when {
                target.contains("windows") || (target.isEmpty() && os.contains("win")) -> "" to ".dll"
                target.contains("apple") || target.contains("darwin") || (target.isEmpty() && os.contains("mac")) -> "lib" to ".dylib"
                else -> "lib" to ".so"
            }

            val builtLibName = "${prefix}Uzyi$suffix"
            val targetDir = if (target.isNotEmpty()) "../target/$target/release" else "../target/release"
            val sourceFile = file("$targetDir/$builtLibName")

            if (sourceFile.exists()) {
                copy {
                    from(sourceFile)
                    into(nativeResourceDir)
                }
            } else {
                logger.warn("Could not find built library at ${sourceFile.absolutePath}")
            }
        }
    }
}

tasks.processResources {
    dependsOn(buildNative)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "com.github.Jadiefication"
            artifactId = "uzyi"
            version = project.version.toString()

            pom {
                name = "Uzyi Virtual Machine"
                description = "A custom virtual machine and instruction set architecture (ISA) developed in Rust with a Kotlin DSL assembler."
                url = "https://github.com/Jadiefication/Uzyi"
                licenses {
                    license {
                        name = "The MIT License"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }
                developers {
                    developer {
                        id = "Jadiefication"
                        name = "Jadie"
                    }
                }
            }
        }
    }
}