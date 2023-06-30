package org.example

import org.example.jictest.TestFlowBase
import org.example.jictest.TestStageExpectation
import java.io.File

class InnerClassTests : TestFlowBase() {
    @org.junit.jupiter.api.Test
    fun testInnerClassDependsOnChanged() {
        val baseline = TestStageExpectation(
            listOf("Main.java", "Library.java", "ClassOwner.java"),
            listOf("before change")
        )
        val afterChange = TestStageExpectation(
            listOf("Library.java", "ClassOwner.java"),
            listOf("1234")
        )
        runMultiStep(
            listOf("src/test/resources/sources/inner_class_modified_indirectly/1", "src/test/resources/sources/inner_class_modified_indirectly/2"),
            listOf(baseline, afterChange)
        )
    }

    @org.junit.jupiter.api.Test
    fun testDependencyOnChangedInnerClass() {
        val baseline = TestStageExpectation(
            listOf("Main.java", "Library.java"),
            listOf("before change")
        )
        val afterChange = TestStageExpectation(
            listOf("Main.java", "Library.java"),
            listOf("1234")
        )
        runMultiStep(
            listOf("src/test/resources/sources/inner_class_modified/1", "src/test/resources/sources/inner_class_modified/2"),
            listOf(baseline, afterChange)
        )
    }
}
