package org.example.analyzer

import org.example.storage.FileComparison
import org.example.storage.MetadataStorage


private data class NewMetadata (
    val fileMetadata: ArrayList<FileComparison>,
    val insertApis: HashMap<String, ArrayList<String>>,
    val insertDependencies: HashMap<String, ArrayList<String>>
) {
    constructor() : this(ArrayList(), HashMap(), HashMap())
}

class StatefulAnalyzer {
    private val newMetadata = NewMetadata()

    val affectedByChange = HashSet<String>()
    //TODO how do we deal with removed files btw
    //might even ignore it in the basic version, to avoid comparing file lists always
    //although it's easy to do, and is worth doing

    //TODO note: it's inefficient to list all files, might want to use sourcepath sometimes
    //TODO rethink the responsibility. in 2-step model, does analyzer even care about the first step?
    fun getFilesToRebuild(sources: List<String>, storage: MetadataStorage): List<String> {
        return sources.filterNot {
            val comparison = storage.getFileComparison(it)
            newMetadata.fileMetadata.add(comparison)
            return@filterNot comparison.isSame
        }
    }

    fun compareSignatures(className: String, signatures: ClassSignatures, storage: MetadataStorage) {
        //TODO unnecessary copy
        newMetadata.insertApis[className] = ArrayList(signatures.published)
        newMetadata.insertDependencies[className] = ArrayList(signatures.used)
        //TODO can detect if there was a significant change to avoid unnecessary reset+write in db

        val newApiSet = signatures.published.toHashSet()
        val knownApis = storage.getClassApis(className)

        val removedApis = knownApis.filterNot { it in newApiSet }
        affectedByChange.addAll(storage.getDependencies(removedApis).map { "$it.java" })

        if (newApiSet.minus(knownApis.toSet()).isNotEmpty()) {
            // hacky way to detect interface changes (see AddedMethodsTests)
            affectedByChange.addAll(storage.getDependencies(listOf(className)).map { "$it.java" })
        }
    }

    fun flushMetadataUpdates(storage: MetadataStorage) {
        storage.updateFileMetadata(newMetadata.fileMetadata)

        for ((className, updates) in newMetadata.insertApis) {
            storage.updateClassApis(className, updates)
        }
        for ((className, updates) in newMetadata.insertDependencies) {
            storage.updateDependencies(className, updates)
        }
    }
}
