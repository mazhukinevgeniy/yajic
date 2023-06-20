package org.example

import org.junit.jupiter.api.Assertions.*
import java.io.File

class ColdStartTest {

    private val outputDir = "yajic_out"

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        val outputDirAsFile = File(outputDir)
        if (outputDirAsFile.exists()) {
            check(outputDirAsFile.deleteRecursively())
        }
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
    }

    @org.junit.jupiter.api.Test
    fun testColdStart() {
        // TODO hardcode first, move to gradle.settings & readme second
        val jdkPath = "C:\\Users\\evgen\\.jdks\\jbr-17.0.6"

        Main.main(arrayOf("-c", "", "-s", "src/test/resources/sources", "-j", jdkPath, "-o", outputDir))

        val outputDirAsFile = File(outputDir)
        assertTrue(outputDirAsFile.exists()) { "tool didn't create the output dir" }

        //TODO check file lists

        //TODO Apache Commons Exec?
        val runner = ProcessBuilder(
            jdkPath + File.separatorChar + "bin" + File.separatorChar + "java",
            "Main"
        )
        runner.directory(outputDirAsFile)

        //TODO clean up everything
        runner.redirectOutput(ProcessBuilder.Redirect.PIPE)
        val process = runner.start()
        val outputReader = process.inputReader()
        process.waitFor()
        val output = outputReader.readLines()
        val errors = process.errorReader().readLines()

        println(errors.joinToString())

        assertEquals(output.size, 1) { "output size" }
        assertEquals(output[0], "42") { "output contents" }

        //TODO require that 1) file is compiled 2) and it actually works 3) and the tool output is expected
        // how do we test for tool stdout? just store it in array or something?
        // better run the thing properly, but meh
        // o, let's write logs
    }
}
