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

        //for asm
       ///////// command.addAll(arrayOf("-source", "1.8", "-target", "1.8"))
        //TODO bootstrapclasspath support

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
        context.results.errors.addAll(errorReader.readLines())
    }
}
