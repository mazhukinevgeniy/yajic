package org.example.analyzer

import org.objectweb.asm.ClassReader
import java.nio.file.Files
import java.nio.file.Paths

data class ClassBasedVerdict(
    val entriesToUpsert: List<String>, //TODO not string
    val rebuildTriggers: List<String>
)

class MetadataScrapper {

    fun checkClassInfo(classFile: String): ClassBasedVerdict {

        val reader = ClassReader(Files.readAllBytes(Paths.get(classFile)))

        //TODO here's the fun part

        return ClassBasedVerdict(emptyList(), emptyList())
    }
}
