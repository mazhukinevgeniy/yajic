package org.example.yajic

import org.apache.commons.io.FileUtils
import java.io.File

class CompilationContext(val classpathStr: String, sourceDirStr: String, jdkDirStr: String?, outputDirStr: String?) {
    val results = DetailedToolResults()
    //TODO: do we do this?

    val sourceDir: File
    val jdkDir: File

    val outputDir: File

    private fun checkedDirectory(path: String): File {
        val dependency = File(path)
        require(dependency.isDirectory) { "$path is not a readable directory" }
        return dependency
    }

    init {
        sourceDir = checkedDirectory(sourceDirStr)

        val javaHome = jdkDirStr ?: System.getenv("JAVA_HOME")
        check (javaHome != null) { "please set path to JDK as an argument or JAVA_HOME environment variable" }
        jdkDir = checkedDirectory(javaHome)

        outputDir = File(outputDirStr ?: "yajic_out")
        if (!outputDir.exists()) {
            outputDir.mkdir()
        } else {
            require(outputDir.isDirectory) { "$outputDirStr is not a readable directory" }
        }
    }

    fun listSources(): List<String> {
        return FileUtils.listFiles(sourceDir, arrayOf("java"), true).map {
            it.canonicalPath
        }
    }
}
