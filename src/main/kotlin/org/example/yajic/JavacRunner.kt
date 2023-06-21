package org.example.yajic

import java.io.File

class JavacRunner {
    //TODO processbuilder etc

    fun execute(sources: List<String>, context: CompilationContext) {
        if (sources.isEmpty()) {
            println("nothing to build")
            //TODO organize logs
            return
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

        val builder = ProcessBuilder(command)
        builder.directory(context.sourceDir)

        //builder.redirectOutput(File)
        //builder.redirectError()

        builder.start().waitFor()
    }
}
