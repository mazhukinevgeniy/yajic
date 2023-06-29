package org.example.jictest

data class TestStageExpectation(
    val compiledFiles: List<String>,
    val programOutput: List<String>,
    val errors: List<String>
) {
    constructor(compiledFiles: List<String>, programOutput: List<String>) : this(
        compiledFiles, programOutput, emptyList()
    )
}
