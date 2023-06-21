package org.example

import org.example.jictest.TestFlowBase
import org.example.jictest.TestStageExpectation

class ColdStartTest : TestFlowBase(
    listOf("src/test/resources/sources"),
    listOf(TestStageExpectation(listOf("Main.java"), listOf("42")))
) {

    @org.junit.jupiter.api.Test
    fun testColdStart() {
        run()
    }
}
