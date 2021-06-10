package io.paloski.logging

import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun traceDirectRunsWithoutException() {
        Dummy.traceLog("test")
    }

    @Test
    fun traceLambdaRunsWithoutException() {
        Dummy.traceLog { "Test" }
    }
}