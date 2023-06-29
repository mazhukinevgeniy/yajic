package org.example.analyzer

import org.example.storage.FileComparison
import org.example.storage.MetadataStorage
import kotlin.math.sign

//TODO optimize imports in the todo-cleaning stage

private data class NewMetadata (
    val fileMetadata: ArrayList<FileComparison>,
    val insertApis: HashMap<String, ArrayList<String>>,
    val insertDependencies: HashMap<String, ArrayList<String>>
) {
    //TODO "apis" and "dependencies" have the same structure always, looks bad
    //well maybe it changes

    //TODO ok the idea is this: drop all info, then insert relevant back

    //TODO it's bad that analyzer doesn't know about storage, let's fix it
    // ehh, maybe just drop it, tbh
    constructor() : this(ArrayList(), HashMap(), HashMap())
}

class StatefulAnalyzer {
    private val newMetadata = NewMetadata()

    val affectedByChange = HashSet<String>()
    //TODO how do we deal with removed files btw
    //might even ignore it in the basic version, to avoid comparing file lists always
    //although it's easy to do, and is worth doing

    //TODO actual signature

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
        //TODO consider and test all core cases
        // 1. api added
        // 2. api removed
        // 3. api changed (aka removal plus addition)

        //TODO unnecessary copy
        newMetadata.insertApis[className] = ArrayList(signatures.published)
        newMetadata.insertDependencies[className] = ArrayList(signatures.used)
        //TODO can detect if there was a significant change to avoid unnecessary reset+write in db

        val newApiSet = signatures.published.toHashSet()
        for (knownApi in storage.getClassApis(className)) {
            if (knownApi !in newApiSet) {
                //TODO awkward logic, fix later
                affectedByChange.addAll(storage.getDependencies(listOf(knownApi)).map { "$it.java" })
            }
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
