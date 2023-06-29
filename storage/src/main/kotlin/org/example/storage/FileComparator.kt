package org.example.storage

import org.apache.commons.io.FileUtils
import java.io.File
import java.util.zip.Adler32

//TODO there shouldn't be name
data class FileComparison(val name: String, val isSame: Boolean, val currentSize: Long, val checksum: Long)

class FileComparator {
    //TODO
    //1. same / not same
    //2. dig deeper - it's analyzer's domain probably, here we just check if it changed

    //TODO what is it really doing in the storage module

    fun makeComparison(file: String, knownChecksum: Long, knownSize: Long): FileComparison {
        val readFile = File(file)
        require(readFile.isFile && readFile.extension == "java")

        val checksum = Adler32()
        val asByteArray = FileUtils.readFileToByteArray(readFile)
        checksum.update(asByteArray)

        val isSame = knownSize == readFile.length() && checksum.value == knownChecksum

        println("checking $file, checksum $knownChecksum -> ${checksum.value}, size $knownSize -> ${readFile.length()}")

        return FileComparison(file, isSame, readFile.length(), checksum.value)
    }
}
