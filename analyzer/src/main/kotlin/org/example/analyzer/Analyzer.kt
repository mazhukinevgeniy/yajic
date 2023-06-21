package org.example.analyzer

import org.example.storage.MetadataStorage

class Analyzer {

    //TODO actual signature

    //TODO note: it's inefficient to list all files, might want to use sourcepath sometimes
    fun getFilesToRebuild(sources: List<String>, storage: MetadataStorage): List<String> {
        return sources
    }
}
