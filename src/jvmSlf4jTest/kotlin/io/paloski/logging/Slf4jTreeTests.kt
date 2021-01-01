package io.paloski.logging

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class Slf4jTreeTests {

    @Test
    fun treeDoesNotEqualArbitraryOtherTree() {
        val otherTree = object : Tree {
            override fun log(tag: String, level: LogLevel, exception: Throwable?, messageProducer: () -> String) {
                TODO("Not yet implemented")
            }
        }
        assertNotEquals<Tree>(otherTree, Slf4jTree(), "Random other tree must not equal a wanted tree")
    }

    @Test
    fun twoTreeInstancesAreEqual() {
        assertEquals(Slf4jTree(), Slf4jTree(),
            "The class is stateless and thus equals should be trustable on any two instances")
    }

}