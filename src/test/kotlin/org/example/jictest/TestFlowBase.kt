package org.example.jictest

import org.example.Main
import org.junit.jupiter.api.Assertions
import java.io.File
import java.nio.file.Files
import java.util.Properties

open class TestFlowBase(sources: List<String>, expectations: List<TestStageExpectation>) {

    private lateinit var outputDir: File

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        outputDir = Files.createTempDirectory("yajic_test").toFile()
        check(outputDir.exists() && outputDir.isDirectory())
        check(Files.list(outputDir.toPath()).findAny().isEmpty)
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
        outputDir.deleteRecursively()
    }

    fun run() {
        Main.main(arrayOf("-c", "", "-s", "src/test/resources/sources", "-j", JDK_DIR, "-o", outputDir.canonicalPath))

        //TODO check file lists

        //TODO Apache Commons Exec?
        val runner = ProcessBuilder(
            JDK_DIR + File.separatorChar + "bin" + File.separatorChar + "java", "Main"
        ).also {
            it.directory(outputDir)
        }

        //TODO clean up everything
        runner.redirectOutput(ProcessBuilder.Redirect.PIPE)
        val process = runner.start()
        val outputReader = process.inputReader()
        process.waitFor()
        val output = outputReader.readLines()
        val errors = process.errorReader().readLines()

        println(errors.joinToString())

        Assertions.assertEquals(output.size, 1) { "output size" }
        Assertions.assertEquals(output[0], "42") { "output contents" }
    }

    companion object {
        private val properties = Properties().also {
            it.load(javaClass.getResourceAsStream("local.properties"))
        }
        val JDK_DIR: String = properties.getProperty("testJdkDir")!!
    }
}
