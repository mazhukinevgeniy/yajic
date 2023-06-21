package org.example

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import org.example.yajic.IncrementalCompilationTool

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("yajic")

        val classpath = parser.option(ArgType.String, "classpath", "c").required()
        val sourceDir = parser.option(ArgType.String, "sourceDir", "s").required()
        val jdkDir = parser.option(ArgType.String, "java_home", "j", "path to jdk. JAVA_HOME is used by default")
        val outputDir = parser.option(ArgType.String, "output", "o", "output directory. yajic_out is used by default")

        parser.parse(args)

        IncrementalCompilationTool().runTool(classpath.value, sourceDir.value, jdkDir.value, outputDir.value)
    }
}
