package org.example.environment

import java.io.File

class CompilationContext(val classpathStr: String, val sourceDirStr: String) {
    private val classpath = ArrayList<File>()
    private val sourceDir: File
    private val jdkDir: File
    //TODO would we actually need these? separate Context and ContextValidator

    private val outputDir: File = File("todo") //TODO this one we'll need

    private fun checkedDirectory(path: String): File {
        val dependency = File(path)
        require(dependency.isDirectory) { "$path is not a readable directory" }
        return dependency
    }

    init {
        for (dependencyDir in classpathStr.split(File.pathSeparatorChar)) {
            classpath.add(checkedDirectory(dependencyDir))
        }
        sourceDir = checkedDirectory(sourceDirStr)

        val javaHome = System.getenv("JAVA_HOME")
        check (javaHome != null) { "please specify path to JDK in JAVA_HOME environment variable" }
        jdkDir = checkedDirectory(javaHome)

        //TODO check that javac is executable and be done with that
    }
}
