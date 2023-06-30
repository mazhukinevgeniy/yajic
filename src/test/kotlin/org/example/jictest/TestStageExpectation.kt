package org.example.jictest

data class TestStageExpectation(
    val compiledFiles: Set<String>,
    val programOutput: List<String>,
    val errors: List<String>
) {
    constructor(compiledFiles: Set<String>, programOutput: List<String>) : this(
        compiledFiles, programOutput, emptyList()
    )
}
