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

    @org.junit.jupiter.api.Test
    fun enforceCheckOfInheritor() {
        val baseline = TestStageExpectation(
            listOf("Main.java", "ILibrary.java", "Library.java", "AbstractLibrary.java"),
            listOf("before change")
        )
        val afterChange = TestStageExpectation(
            listOf("ILibrary.java", "Library.java", "AbstractLibrary.java"),
            programOutput = emptyList(),
            errors = listOf("Library is not abstract")
        )
        val afterFix = TestStageExpectation(
            listOf("Library.java"),
            programOutput = listOf("done")
        )
        runMultiStep(
            listOf(
                "src/test/resources/sources/add_method_to_interface/1",
                "src/test/resources/sources/add_method_to_interface/2",
                "src/test/resources/sources/add_method_to_interface/3"
            ),
            listOf(baseline, afterChange, afterFix)
        )

        //TODO: support case where indirect implementor of an interface should be rebuilt
    }
}
