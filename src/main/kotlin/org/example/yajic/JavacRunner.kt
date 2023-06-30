package org.example.yajic

import java.io.File

data class JavacReport(
    val errors: List<String>
)

class JavacRunner {
    fun execute(sources: Iterable<String>, context: CompilationContext) : JavacReport {
        if (!sources.iterator().hasNext()) {
            return JavacReport(emptyList())
        }

        val command = mutableListOf<String>(
            context.jdkDir.canonicalPath + File.separator + "bin" + File.separator + "javac",
            "-d",
            context.outputDir.canonicalPath
        )

        if (context.classpathStr.isNotEmpty()) {
            command.add("-cp")
            command.add(context.classpathStr)
        }

        command.addAll(sources)

        println("running javac as $command")

        val builder = ProcessBuilder(command)
        builder.directory(context.sourceDir)

        val javacProcess = builder.start()
        val errorReader = javacProcess.errorReader()

        javacProcess.waitFor()

        return JavacReport(
            errorReader.readLines()
        )
    }
}
