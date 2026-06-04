plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
}

group = "io.jadie"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}

val nativeResourceDir = layout.projectDirectory.dir("src/main/resources/native")

val buildNative by tasks.registering {
    description = ""
    doLast {
        val os = System.getProperty("os.name").lowercase()
        val targets = if (System.getenv("JITPACK") == "true") {
            listOf(
                "x86_64-unknown-linux-gnu",
                "x86_64-pc-windows-gnu"
            )
        } else {
            listOf("")
        }

        targets.forEach { target ->
            val targetArgs = mutableListOf("cargo", "build", "--release")
            if (target.isNotEmpty()) {
                targetArgs.add("--target")
                targetArgs.add(target)
            }
            
            val processBuilder = ProcessBuilder(targetArgs)
            processBuilder.directory(File(".."))
            processBuilder.redirectErrorStream(true)
            processBuilder.inheritIO()
            val process = processBuilder.start()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw GradleException("Cargo build failed with exit code $exitCode")
            }

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