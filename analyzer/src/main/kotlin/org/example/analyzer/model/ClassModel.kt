package org.example.analyzer.model

data class ClassModel(
    val publicMethods: Set<String>,
    val publicFields: Set<String>,
    val ancestors: Set<ClassModel>
) {
    //TODO: ? need to research
}
