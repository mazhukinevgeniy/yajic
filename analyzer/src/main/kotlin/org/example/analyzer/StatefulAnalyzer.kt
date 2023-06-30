package org.example.analyzer

import org.example.storage.FileMetadata
import org.example.storage.MetadataStorage


private data class NewMetadata (
    val fileMetadata: HashMap<String, FileMetadata>,
    val insertApis: HashMap<String, Set<String>>,
    val insertDependencies: HashMap<String, Set<String>>
) {
    constructor() : this(HashMap(), HashMap(), HashMap())
}

class StatefulAnalyzer {
    private val comparator = FileComparator()

    private val newMetadata = NewMetadata()

    val affectedByChange = HashSet<String>()

    fun getFilesToRebuild(sources: List<String>, storage: MetadataStorage): List<String> {
        return sources.filterNot {
            val comparison = comparator.makeComparison(it, storage.getFileMetadata(it))
            newMetadata.fileMetadata[it] = comparison.currentMetadata
            return@filterNot comparison.isSame
        }
    }

    fun compareSignatures(className: String, signatures: ClassSignatures, storage: MetadataStorage) {
        newMetadata.insertApis[className] = signatures.published
        newMetadata.insertDependencies[className] = signatures.used

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
