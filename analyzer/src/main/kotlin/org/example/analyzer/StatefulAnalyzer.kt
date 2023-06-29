package org.example.analyzer

import org.example.storage.FileComparison
import org.example.storage.MetadataStorage
//TODO optimize imports in the todo-cleaning stage

data class NewMetadata (
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

    //TODO ok actually it's crazy inefficient, go the right way
    fun compareSignatures(className: String, signatures: ClassSignatures, knownApis: List<String>, knownDependencies: List<String>) {
        //TODO cleanup
        // all cases
        // 1. api added
        // 2. api removed
        // 3. api changed (aka removal plus addition)

        //okay, so wait, do we even need signatures
        //let's just track dependencies for now
        //it would be messy in inheritance scenarios though
        //idk idk
        //TODO just write tests

        /*
    val fileMetadata: ArrayList<FileComparison>,
    val insertApis: HashMap<String, ArrayList<String>>,
    val deleteApis: HashMap<String, ArrayList<String>>,
    val insertDependencies: HashMap<String, ArrayList<String>>,
    val deleteDependencies: HashMap<String, ArrayList<String>>*/




//*val className = rebuilt.substring(0, rebuilt.length - ".java".length)
//            val classInfo = metadataExtractor.extractSignatures(
//                "${context.outputDir.canonicalPath}${File.separator}$className.class"
//            )
//            val knownApis = storage.getClassApis(className)
//            val knownDependencies = storage.getDependencies(classInfo.published)*/
    }

    //todo what's data type?
    fun getDataUpdates(): List<FileComparison> {
        return newMetadata.fileMetadata
    }
}
