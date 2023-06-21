package org.example.analyzer

import org.example.storage.FileComparison
import org.example.storage.MetadataStorage

class Analyzer {
    private val updates = ArrayList<FileComparison>()

    //TODO actual signature

    //TODO note: it's inefficient to list all files, might want to use sourcepath sometimes
    fun getFilesToRebuild(sources: List<String>, storage: MetadataStorage): List<String> {
        return sources.filterNot {
            val comparison = storage.getFileComparison(it)
            updates.add(comparison)
            return@filterNot comparison.isSame
        }
    }

    //todo what's data type?
    fun getDataUpdates(): List<FileComparison> {
        return updates
    }
}
