package org.example.analyzer

import org.apache.commons.io.FileUtils
import org.example.storage.FileMetadata
import java.io.File
import java.util.zip.Adler32

data class FileComparison (
    val isSame: Boolean,
    val currentMetadata: FileMetadata
)

class FileComparator {

    fun makeComparison(file: String, storedMetadata: FileMetadata): FileComparison {
        val readFile = File(file)
        require(readFile.isFile && readFile.extension == "java")

        val checksum = Adler32()
        val asByteArray = FileUtils.readFileToByteArray(readFile)
        checksum.update(asByteArray)

        val newMetadata = FileMetadata(checksum.value, readFile.length())
        val isSame = storedMetadata == newMetadata

        return FileComparison(isSame, newMetadata)
    }
}
