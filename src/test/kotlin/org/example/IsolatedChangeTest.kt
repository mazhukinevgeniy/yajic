package org.example

import org.example.jictest.TestFlowBase
import org.example.jictest.TestStageExpectation

class IsolatedChangeTest : TestFlowBase() {
    @org.junit.jupiter.api.Test
    fun testIsolatedChange() {
        val baseline = TestStageExpectation(
            listOf("Main.java", "Library.java"),
            listOf("before change")
        )
        val afterChange = TestStageExpectation(
            listOf("Library.java"),
            listOf("after change")
        )
        runMultiStep(
            listOf("src/test/resources/sources/isolatedchange/1", "src/test/resources/sources/isolatedchange/2"),
            listOf(baseline, afterChange)
        )
    }

    @org.junit.jupiter.api.Test
    fun testReturnTypeChange() {
        val baseline = TestStageExpectation(
            listOf("Main.java", "Library.java"),
            listOf("before change")
        )
        val afterChange = TestStageExpectation(
            listOf("Main.java", "Library.java"),
            listOf("2000")
        )
        runMultiStep(
            listOf("src/test/resources/sources/changereturntype/1", "src/test/resources/sources/changereturntype/2"),
            listOf(baseline, afterChange)
        )
    }

    @org.junit.jupiter.api.Test
    fun testChangedConstructor() {
        val baseline = TestStageExpectation(
            listOf("Main.java", "Library.java"),
            listOf("before change")
        )
        val afterChange = TestStageExpectation(
            listOf("Main.java", "Library.java"),
            programOutput = emptyList(),
            errors = listOf("constructor Library in class Library cannot be applied to given types")
        )
        val afterFix = TestStageExpectation(
            listOf("Main.java"),
            listOf("1111", "15")
        )
        runMultiStep(
            listOf(
                "src/test/resources/sources/changeconstructor/1",
                "src/test/resources/sources/changeconstructor/2",
                "src/test/resources/sources/changeconstructor/3"
            ),
            listOf(baseline, afterChange, afterFix)
        )
    }
}
