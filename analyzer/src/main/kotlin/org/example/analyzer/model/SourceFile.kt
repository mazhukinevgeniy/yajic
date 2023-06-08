package org.example.analyzer.model

// begin with assumption: sourcefile <-> class
//TODO: kotlin? internal classes? lambdas?
data class SourceFile(val path: String, val classModel: ClassModel) {
}
