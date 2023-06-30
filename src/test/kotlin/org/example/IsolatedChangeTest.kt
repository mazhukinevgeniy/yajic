package org.example

import org.example.jictest.TestFlowBase
import org.example.jictest.TestStageExpectation

class IsolatedChangeTest : TestFlowBase() {
    @org.junit.jupiter.api.Test
    fun testIsolatedChange() {
        val baseline = TestStageExpectation(
            setOf("Main.java", "Library.java"),
            listOf("before change")
        )
        val afterChange = TestStageExpectation(
            setOf("Library.java"),
            listOf("after change")
        )
        runMultiStep(
            listOf("src/test/resources/sources/isolated_change/1", "src/test/resources/sources/isolated_change/2"),
            listOf(baseline, afterChange)
        )
    }

    @org.junit.jupiter.api.Test
    fun testReturnTypeChange() {
        val baseline = TestStageExpectation(
            setOf("Main.java", "Library.java"),
            listOf("before change")
        )
        val afterChange = TestStageExpectation(
            setOf("Main.java", "Library.java"),
            listOf("2000")
        )
        runMultiStep(
            listOf("src/test/resources/sources/change_return_type/1", "src/test/resources/sources/change_return_type/2"),
            listOf(baseline, afterChange)
        )
    }

    @org.junit.jupiter.api.Test
    fun testChangedConstructor() {
        val baseline = TestStageExpectation(
            setOf("Main.java", "Library.java"),
            listOf("before change")
        )
        val afterChange = TestStageExpectation(
            setOf("Main.java", "Library.java"),
            programOutput = emptyList(),
            errors = listOf("constructor Library in class Library cannot be applied to given types")
        )
        val afterFix = TestStageExpectation(
            setOf("Main.java"),
            listOf("1111", "15")
        )
        runMultiStep(
            listOf(
                "src/test/resources/sources/change_constructor/1",
                "src/test/resources/sources/change_constructor/2",
                "src/test/resources/sources/change_constructor/3"
            ),
            listOf(baseline, afterChange, afterFix)
        )
    }
}
