package io.paloski.logging

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlatformTests {
    @Test
    fun defaultTreeCanBePlanted() {
        Forest.plantDefaultTree()
    }

    @Test
    fun defaultTreeAppearsInListAfterPlanting() {
        assertTrue(Forest.trees.isEmpty(), "Forest must start empty")
        Forest.plantDefaultTree()
        assertEquals(1, Forest.trees.size, "Forest must end with a size of 1")
    }
}
