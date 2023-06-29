package org.example.jictest

import org.apache.commons.io.FileUtils
import org.example.yajic.IncrementalCompilationTool
import org.junit.jupiter.api.Assertions
import java.io.File
import java.nio.file.Files
import java.util.*

open class TestFlowBase() {

    private lateinit var outputDir: File
    private lateinit var sourceDir: File

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        outputDir = Files.createTempDirectory("yajic_test").toFile()
        sourceDir = Files.createTempDirectory("yajic_test_sources").toFile()
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
        outputDir.deleteRecursively()
        sourceDir.deleteRecursively()
    }

    private fun testCompiledProgram(expectation: TestStageExpectation) {
        val runner = ProcessBuilder(
            JDK_DIR + File.separatorChar + "bin" + File.separatorChar + "java", "Main"
        ).also {
            it.directory(outputDir)
        }

        val process = runner.start()
        val outputReader = process.inputReader()
        val errorReader = process.errorReader()
        process.waitFor()
        val output = outputReader.readLines()
        val errors = errorReader.readLines()

        Assertions.assertEquals(emptyList<String>(), errors) { "program ran successfully" }
        Assertions.assertEquals(expectation.programOutput, output) { "output after incremental compilation" }
    }

    fun runMultiStep(sources: List<String>, expectations: List<TestStageExpectation>) {
        require(sources.size == expectations.size)
        require(sources.isNotEmpty())

        for (i in sources.indices) {
            println("===\nstep $i\n===")
            //TODO consider case: if a source file is removed, should we remove its output .class file? probably yes
            sourceDir.deleteRecursively()
            FileUtils.copyDirectory(File(sources[i]), sourceDir)

            val result = IncrementalCompilationTool().runTool(
                "",
                sourceDir.canonicalPath,
                JDK_DIR,
                outputDir.canonicalPath
            )

            // error messages might be jdk-dependant, so let's just check for presence of errors
            if (expectations[i].errors.isNotEmpty()) {
                Assertions.assertNotEquals(0, result.errors.size) { "expected compilation errors" }
                println("errors: \n" + result.errors.joinToString("\n"))
            } else {
                Assertions.assertEquals(emptyList<String>(), result.errors)

                testCompiledProgram(expectations[i])
            }

            //TODO make it set-first
            Assertions.assertEquals(expectations[i].compiledFiles.toSet(), result.compiledFiles.toSet()) {
                "set of compiled files"
            }
        }
    }

    fun runSimple(source: String, expectation: TestStageExpectation) {
        runMultiStep(listOf(source), listOf(expectation))
    }

    companion object {
        private val properties = Properties().also {
            it.load(javaClass.getResourceAsStream("local.properties"))
        }
        val JDK_DIR: String = properties.getProperty("testJdkDir")!!
    }
}
