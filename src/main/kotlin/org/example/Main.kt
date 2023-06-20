package org.example

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import org.example.analyzer.Analyzer
import org.example.environment.CompilationContext
import org.example.environment.JavacRunner
import org.example.storage.MetadataStorage

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("yajic")

        val classpath = parser.option(ArgType.String, "classpath", "c").required()
        val sourceDir = parser.option(ArgType.String, "sourceDir", "s").required()
        val jdkDir = parser.option(ArgType.String, "java_home", "j", "path to jdk. JAVA_HOME is used by default")
        val outputDir = parser.option(ArgType.String, "output", "o", "output directory. yajic_out is used by default")

        parser.parse(args)

        val context = CompilationContext(classpath.value, sourceDir.value, jdkDir.value, outputDir.value)

        val storage = MetadataStorage()
        val analyzer = Analyzer()

        val filesToRebuild = analyzer.getFilesToRebuild(context, storage)

        JavacRunner().execute(filesToRebuild, context)

        // flow is like this
        // environment validates the inputs, provides canonical data about paths etc
        // storage tells what we already know
        // analyzer decides what to do
        // environment communicates with javac
        // storage updates metadata, depending on the result
        // tool provides diagnostics & returns

            //TODO do we handle classpath changes? yes we must, but think about it
    }
}
