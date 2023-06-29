package org.example.yajic

import org.example.analyzer.StatefulAnalyzer
import org.example.analyzer.MetadataExtractor
import org.example.storage.MetadataStorage
import java.io.File

class IncrementalCompilationTool {

    fun runTool(
        classpathStr: String, sourceDirStr: String, jdkDirStr: String?, outputDirStr: String?
    ): DetailedToolResults {
        //TODO environment validates the inputs, provides canonical data about paths etc
        //TODO remove todos
        val context = CompilationContext(classpathStr, sourceDirStr, jdkDirStr, outputDirStr)

        val storage = MetadataStorage.openProject(context.outputDir.canonicalPath)
        val analyzer = StatefulAnalyzer()

        //TODO storage tells what we already know, analyzer decides what to do
        val filesToRebuild = analyzer.getFilesToRebuild(context.listSources(), storage)
        context.results.compiledFiles = filesToRebuild.map {
            it.substring(context.sourceDir.canonicalPath.length + 1)
        }

        JavacRunner().execute(filesToRebuild, context)
        //TODO ok, new plan. since analysis is based on the compiled .class files, we must detect diffs, compile them,
        //then check for cascade changes
        //TODO can we really optimize it without parsing the sources?

        val metadataExtractor = MetadataExtractor()
        for (rebuilt in context.results.compiledFiles) {
            val className = rebuilt.substring(0, rebuilt.length - ".java".length)
            val classInfo = metadataExtractor.extractSignatures(
                "${context.outputDir.canonicalPath}${File.separator}$className.class"
            )
            val knownApis = storage.getClassApis(className)
            val knownDependencies = storage.getDependencies(classInfo.published)


        }

        //TODO receive compilation result; storage updates metadata, depending on the result

        //assuming that compilation is always successful:
        storage.updateFileMetadata(analyzer.getDataUpdates())

        //TODO tool provides diagnostics & returns

        return context.results
    }
}
