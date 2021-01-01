package io.paloski.logging.io.paloski.logging

import io.paloski.logging.Forest
import io.paloski.logging.plantDefaultTree
import io.paloski.logging.uproot
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
        Forest.trees.forEach { it.uproot() }
        assertTrue(Forest.trees.isEmpty(), "Forest must start empty")
        Forest.plantDefaultTree()
        assertEquals(1, Forest.trees.size, "Forest must end with a size of 1")
    }
}
