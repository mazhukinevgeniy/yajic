package org.example

import org.junit.jupiter.api.Assertions.*

class ColdStartTest {

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {

    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
    }

    @org.junit.jupiter.api.Test
    fun testColdStart() {
        // TODO hardcode first, move to gradle.settings & readme second
        Main.main(arrayOf("-c", "", "-s", "src/test/resources/sources", "-j", "C:\\Users\\evgen\\.jdks"))
    }
}
