package org.example

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import org.example.environment.CompilationContext

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("yajic")

        val classpath = parser.option(ArgType.String, "classpath", "c").required()
        val sourceDir = parser.option(ArgType.String, "sourceDir", "s").required()
        val jdkDir = parser.option(ArgType.String, "java_home", "j", "path to jdk. JAVA_HOME is used by default")

        parser.parse(args)

        val context = CompilationContext(classpath.value, sourceDir.value, jdkDir.value)

        // flow is like this
        // environment validates the inputs, provides canonical data about paths etc
        // storage tells what we already know
        // analyzer decides what to do
        // environment communicates with javac
        // storage updates metadata, depending on the result
        // tool provides diagnostics & returns
    }
}
