package org.example.environment

import org.apache.commons.io.FileUtils
import java.io.File

class CompilationContext(val classpathStr: String, val sourceDirStr: String, jdkDirStr: String?, outputDirStr: String?) {
    private val classpath = ArrayList<File>()
    private val sourceDir: File
    val jdkDir: File
    //TODO would we actually need these? separate Context and ContextValidator

    val outputDir: File

    private fun checkedDirectory(path: String): File {
        val dependency = File(path)
        require(dependency.isDirectory) { "$path is not a readable directory" }
        return dependency
    }

    init {
        for (dependencyDir in classpathStr.split(File.pathSeparatorChar)) {
            //classpath.add(checkedDirectory(dependencyDir))
            //TODO classpath requirements are less strict https://docs.oracle.com/javase/8/docs/technotes/tools/unix/classpath.html
            //actually, we can delegate the validation to javac
        }
        sourceDir = checkedDirectory(sourceDirStr)

        val javaHome = jdkDirStr ?: System.getenv("JAVA_HOME")
        check (javaHome != null) { "please specify path to JDK in JAVA_HOME environment variable" }
        jdkDir = checkedDirectory(javaHome)

        outputDir = File(outputDirStr ?: "yajic_out")
        if (!outputDir.exists()) {
            outputDir.mkdir()
        } else {
            require(outputDir.isDirectory) { "$outputDirStr is not a readable directory" }
        }

        //TODO check that javac is executable and be done with that
    }

    fun listSources(): List<String> {
        return FileUtils.listFiles(sourceDir, arrayOf("java"), true).map { it.canonicalPath }
    }
}
