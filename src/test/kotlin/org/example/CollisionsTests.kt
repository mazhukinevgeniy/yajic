package org.example

import org.example.jictest.TestFlowBase
import org.example.jictest.TestStageExpectation
import java.io.File

class CollisionsTests : TestFlowBase() {
    @org.junit.jupiter.api.Test
    fun testClassNameCollision() {
        //tests that tool works with packages properly. based on "change return type" test
        val slash = File.separator

        val baseline = TestStageExpectation(
            setOf("Main.java", "org${slash}example${slash}Library.java", "com${slash}example${slash}math${slash}Library.java"),
            listOf("before change")
        )
        val afterChange = TestStageExpectation(
            setOf("org${slash}example${slash}Library.java", "com${slash}example${slash}math${slash}Library.java"),
            listOf("2")
        )
        runMultiStep(
            listOf("src/test/resources/sources/class_name_collision/1", "src/test/resources/sources/class_name_collision/2"),
            listOf(baseline, afterChange)
        )
    }
}
