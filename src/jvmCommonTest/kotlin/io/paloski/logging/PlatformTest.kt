package io.paloski.logging

import org.junit.Test
import kotlin.test.assertTrue

class PlatformTest {

    @Test
    fun allServiceLoadedTreesCanBePlanted() {
        Forest.plantAllServiceTrees()
        assertTrue(Forest.trees.isNotEmpty(), "At least one tree must be planted by service loading")
    }

}