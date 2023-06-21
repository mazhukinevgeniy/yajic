package org.example.yajic

import org.example.analyzer.Analyzer
import org.example.storage.MetadataStorage

class IncrementalCompilationTool {

    fun runTool(
        classpathStr: String, sourceDirStr: String, jdkDirStr: String?, outputDirStr: String?
    ): DetailedToolResults {
        //TODO environment validates the inputs, provides canonical data about paths etc
        val context = CompilationContext(classpathStr, sourceDirStr, jdkDirStr, outputDirStr)

        val storage = MetadataStorage.openProject(context.outputDir.canonicalPath)
        val analyzer = Analyzer()

        //TODO storage tells what we already know, analyzer decides what to do
        val filesToRebuild = analyzer.getFilesToRebuild(context.listSources(), storage)
        context.results.compiledFiles = filesToRebuild.map {
            it.substring(context.sourceDir.canonicalPath.length + 1)
        }

        JavacRunner().execute(filesToRebuild, context)
        //TODO ok, new plan. since analysis is based on the compiled .class files, we must detect diffs, compile them,
        //then check for cascade changes

        //TODO receive compilation result; storage updates metadata, depending on the result

        //assuming that compilation is always successful:
        storage.update(analyzer.getDataUpdates())

        //TODO tool provides diagnostics & returns

        return context.results
    }
}
