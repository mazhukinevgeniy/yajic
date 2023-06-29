package org.example.yajic

import org.example.analyzer.ClassMetadataExtractor
import org.example.analyzer.StatefulAnalyzer
import org.example.storage.MetadataStorage
import java.io.File

class IncrementalCompilationTool {

    private val metadataExtractor = ClassMetadataExtractor()

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
        }.toSet()

        JavacRunner().execute(filesToRebuild, context)
        //TODO can we really optimize it without parsing the sources?

        //TODO preferably don't use raw strings for file paths
        analyzeNewClassFiles(context.results.compiledFiles, analyzer, storage, context)

        val secondStageNoPrefix = analyzer.affectedByChange.minus(context.results.compiledFiles)
        val secondStageSources = secondStageNoPrefix.map {
            "${context.sourceDir.canonicalPath}${File.separator}$it"
        }
        JavacRunner().execute(secondStageSources, context)

        context.results.compiledFiles = context.results.compiledFiles.plus(analyzer.affectedByChange)
        //TODO messy use of state here, write proper code
        analyzeNewClassFiles(secondStageNoPrefix, analyzer, storage, context)

        //assuming that compilation is always successful:
        analyzer.flushMetadataUpdates(storage)

        //TODO tool provides diagnostics & returns

        return context.results
    }

    private fun analyzeNewClassFiles(sources: Iterable<String>, analyzer: StatefulAnalyzer, storage: MetadataStorage, context: CompilationContext) {
        for (rebuilt in sources) {
            val className = rebuilt.substring(0, rebuilt.length - ".java".length)
            val classInfo = metadataExtractor.extractSignatures(
                "${context.outputDir.canonicalPath}${File.separator}$className.class"
            )

            analyzer.compareSignatures(className, classInfo, storage)
        }
    }
}
