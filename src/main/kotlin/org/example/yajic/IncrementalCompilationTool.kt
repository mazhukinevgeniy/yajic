package org.example.yajic

import org.example.analyzer.Analyzer
import org.example.storage.MetadataStorage

class IncrementalCompilationTool {

    fun runTool(
        classpathStr: String, sourceDirStr: String, jdkDirStr: String?, outputDirStr: String?
    ): DetailedToolResults {
        val context = CompilationContext(classpathStr, sourceDirStr, jdkDirStr, outputDirStr)

        val storage = MetadataStorage()
        val analyzer = Analyzer()

        val filesToRebuild = analyzer.getFilesToRebuild(context.listSources(), storage)
        context.results.compiledFiles = filesToRebuild.map { it.substring(context.sourceDirStr.length + 1) }

        JavacRunner().execute(filesToRebuild, context)

        // flow is like this
        // environment validates the inputs, provides canonical data about paths etc
        // storage tells what we already know
        // analyzer decides what to do
        // environment communicates with javac
        // storage updates metadata, depending on the result
        // tool provides diagnostics & returns

        //TODO do we handle classpath changes? yes we must, but think about it

        return context.results
    }
}
