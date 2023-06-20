package org.example.analyzer

import org.example.environment.CompilationContext
import org.example.storage.MetadataStorage

class Analyzer {

    //TODO actual signature

    //TODO note: it's inefficient to list all files, might want to use sourcepath sometimes
    fun getFilesToRebuild(context: CompilationContext, storage: MetadataStorage): List<String> {
        val allSourceFiles = context.listSources()

        return allSourceFiles
    }
}
