package org.example

import org.example.jictest.TestFlowBase
import org.example.jictest.TestStageExpectation
import java.io.File

class AddedMethodsTests : TestFlowBase() {
    @org.junit.jupiter.api.Test
    fun testNoAdditionalBuildingNeeded() {
        val baseline = TestStageExpectation(
            listOf("Main.java", "Library.java"),
            listOf("before change")
        )
        val afterChange = TestStageExpectation(
            listOf("Library.java"),
            listOf("before change")
        )
        runMultiStep(
            listOf("src/test/resources/sources/add_method_safely/1", "src/test/resources/sources/add_method_safely/2"),
            listOf(baseline, afterChange)
        )
    }
}
