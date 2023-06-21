package org.example

import org.example.jictest.TestFlowBase
import org.example.jictest.TestStageExpectation

class BasicTests : TestFlowBase() {

    @org.junit.jupiter.api.Test
    fun testColdStart() {
        runSimple("src/test/resources/sources", TestStageExpectation(listOf("Main.java"), listOf("42")))
    }

    @org.junit.jupiter.api.Test
    fun testNoChanges() {
        val expectBuild = TestStageExpectation(listOf("Main.java"), listOf("42"))
        val expectNoOp = TestStageExpectation(emptyList(), listOf("42"))
        runMultiStep(
            listOf("src/test/resources/sources", "src/test/resources/sources"),
            listOf(expectBuild, expectNoOp)
        )
    }
}
