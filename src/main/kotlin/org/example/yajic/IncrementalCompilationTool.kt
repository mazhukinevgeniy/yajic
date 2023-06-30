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
        val context = CompilationContext(classpathStr, sourceDirStr, jdkDirStr, outputDirStr)

        val storage = MetadataStorage.openProject(context.outputDir.canonicalPath)
        val analyzer = StatefulAnalyzer()

        val filesToRebuild = analyzer.getFilesToRebuild(context.listSources(), storage)

        // step 1: compile directly changed files

        val report = JavacRunner().execute(filesToRebuild, context)
        if (report.errors.isNotEmpty()) {
            println("build failed\n" + report.errors.joinToString("\n"))
            return DetailedToolResults(emptySet(), ArrayList(report.errors))
        }

        val results = DetailedToolResults(
            filesToRebuild.map {
                it.substring(context.sourceDir.canonicalPath.length + 1)
            }.toSet(),
            ArrayList()
        )

        // step 2: compile dependencies to either ensure that code is consistent, or force compilation error

        analyzeNewClassFiles(results.attemptedToCompile, analyzer, storage, context)

        val secondStageNoPrefix = analyzer.affectedByChange.minus(results.attemptedToCompile)
        results.attemptedToCompile = results.attemptedToCompile.plus(analyzer.affectedByChange)

        val secondStageSources = secondStageNoPrefix.map {
            "${context.sourceDir.canonicalPath}${File.separator}$it"
        }
        val secondReport = JavacRunner().execute(secondStageSources, context)
        if (secondReport.errors.isNotEmpty()) {
            println("build of dependent classes failed\n" + secondReport.errors.joinToString("\n"))
            results.errors.addAll(secondReport.errors)
        } else {
            analyzeNewClassFiles(secondStageNoPrefix, analyzer, storage, context)
        }

        analyzer.flushMetadataUpdates(storage)

        return results
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
