package io.paloski.logging

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class KLogTest {

    object Dummy

    //Simple tests: Make sure we can call the function without it blowing up
    @Test
    fun infoDirectRunsWithoutException() {
        Dummy.infoLog("test")
    }

    @Test
    fun infoLambdaRunsWithoutException() {
        Dummy.infoLog { "Test" }
    }

    @Test
    fun debugDirectRunsWithoutException() {
        Dummy.debugLog("test")
    }

    @Test
    fun debugLambdaRunsWithoutException() {
        Dummy.debugLog { "Test" }
    }

    @Test
    fun warnDirectRunsWithoutException() {
        Dummy.warningLog("test")
    }

    @Test
    fun warnLambdaRunsWithoutException() {
        Dummy.warningLog { "Test" }
    }

    @Test
    fun errorDirectRunsWithoutException() {
        Dummy.errorLog("test")
    }

    @Test
    fun errorLambdaRunsWithoutException() {
        Dummy.errorLog { "Test" }
    }

    @Test
    fun fatalDirectRunsWithoutException() {
        Dummy.fatalLog("test")
    }

    @Test
    fun fatalLambdaRunsWithoutException() {
        Dummy.fatalLog { "Test" }
    }

    @Test
    fun forestTreeCannotBePlantedIntoForest() { //The set of sets which cannot contain itself...
        try {
            Forest.plant(Forest.asTree())
            fail("The forest tree must not be able to be planted into itself")
        } catch(exp : IllegalArgumentException) {
            //Success
        }

        try {
            Forest.asTree().plant()
            fail("The forest tree must not be able to be planted")
        } catch(exp : IllegalArgumentException) {
            //Success
        }
    }

    @Test
    fun defaultTreeCanBePlanted() {
        makeNativeTree().plant()
    }

    @Test
    fun defaultTreeAppearsInListAfterPlanting() {
        val tree = makeNativeTree()
        tree.plant()

        assertTrue("Forest must contain the default native tree") { Forest.trees.contains(tree) }
    }

    @Test
    fun plantedTreeCanBeUprooted() {
        val tree = makeNativeTree()
        tree.plant()
        tree.uproot()
    }
}