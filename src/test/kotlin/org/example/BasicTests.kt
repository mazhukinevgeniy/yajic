package org.example

import org.example.jictest.TestFlowBase
import org.example.jictest.TestStageExpectation

class BasicTests : TestFlowBase() {

    @org.junit.jupiter.api.Test
    fun testColdStart() {
        runSimple("src/test/resources/sources/basic_tests", TestStageExpectation(setOf("Main.java"), listOf("42")))
    }

    @org.junit.jupiter.api.Test
    fun testNoChanges() {
        val expectBuild = TestStageExpectation(setOf("Main.java"), listOf("42"))
        val expectNoOp = TestStageExpectation(emptySet(), listOf("42"))
        runMultiStep(
            listOf("src/test/resources/sources/basic_tests", "src/test/resources/sources/basic_tests"),
            listOf(expectBuild, expectNoOp)
        )
    }
}
