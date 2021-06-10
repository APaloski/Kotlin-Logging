package io.paloski.logging

import java.util.concurrent.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class LoggingProxyTest {

    @Test
    fun loggingProxyProperlyPassesArgumentsAndResultsThrough() {
        val memoryTreeRef = AtomicReference<MemoryTree>()
        val expectedResult = false
        val impl = MemoryImpl(expectedResult)
        val loggedProxyImpl = impl.logged<ToProxy>(
            callTracingLevel = LogLevel.DEBUG,
            errorLevel = LogLevel.ERROR,
            treeFactory = {
                val tree = MemoryTree()
                memoryTreeRef.set(tree)
                tree
            }
        )
        val expectedArg1 = "TestInput"
        val expectedArg2 = 2
        val actualResult = loggedProxyImpl.dummy(expectedArg1, expectedArg2)

        assertEquals(expectedResult, actualResult)
        assertEquals(1, impl.calls.size)
        assertEquals(expectedArg1, impl.calls.first().arg1)
        assertEquals(expectedArg2, impl.calls.first().arg2)

        //Get all our logged records
        val byLevel = memoryTreeRef.get().records.groupBy { it.level }
        //Make sure nothing but debug (our preferred) came through
        for (value in LogLevel.values()) {
            when(value) {
                LogLevel.DEBUG -> {
                    //We expect an input and output message, both need to log on the correct class name and the input
                    // should include the params and the output should include the return value. We don't have many
                    // other requirements
                    val inputs = byLevel[value].orEmpty()[0]
                    assertTrue(inputs.message.contains(expectedArg1) && inputs.message.contains(expectedArg2.toString()), "Input Message must contain the expected arguments")
                    val outputs = byLevel[value].orEmpty()[1]
                    assertTrue(inputs.message.contains(expectedArg1) && inputs.message.contains(expectedArg2.toString()), "Output Message must contain the return value")
                }
                else -> assertEquals(0, byLevel[value].orEmpty().size, "Level $value must have no logged records")
            }
        }
    }

    @Test
    fun loggingProxyPassesThroughExceptionAndLogsIt() {
        val memoryTreeRef = AtomicReference<MemoryTree>()
        val expectedException = IllegalStateException()
        val impl = MemoryImpl(expectedException = expectedException)
        val loggedProxyImpl = impl.logged<ToProxy>(
            treeFactory = {
                val tree = MemoryTree()
                memoryTreeRef.set(tree)
                tree
            },
            callTracingLevel = LogLevel.INFO,
            errorLevel = LogLevel.ERROR
        )
        val expectedArg1 = "TestInput"
        val expectedArg2 = 2
        try {
            loggedProxyImpl.dummy(expectedArg1, expectedArg2)
            fail("Didn't get expected exception")
        } catch (exp : Exception) {
            assertEquals(expectedException, exp)
            assertEquals(1, impl.calls.size)
            assertEquals(expectedArg1, impl.calls.first().arg1)
            assertEquals(expectedArg2, impl.calls.first().arg2)
        }

        //Get all our logged records
        val byLevel = memoryTreeRef.get().records.groupBy { it.level }
        for(value in LogLevel.values()) {
            when(value) {
                LogLevel.INFO -> {
                    //We expect an input and *NO* output message, both need to log on the correct class name and the input
                    // should include the params. We don't have many other requirements
                    val inputs = byLevel[value].orEmpty()[0]
                    assertTrue(inputs.message.contains(expectedArg1) && inputs.message.contains(expectedArg2.toString()), "Input Message must contain the expected arguments")
                }
                LogLevel.ERROR -> {
                    //We need to have an exception message with our expected exception contained
                    val expMessage = byLevel[value].orEmpty()[0]
                    assertEquals(expMessage.exception, expectedException)
                }
                else -> assertEquals(0, byLevel[value].orEmpty().size , "Level $value must have no logged records")
            }
        }
    }

}

class MemoryTree : Tree {
    private val _records = mutableListOf<LogRecord>()
    val records : List<LogRecord>
        get() = _records

    override fun log(level: LogLevel, exception: Throwable?, messageProducer: () -> String) {
        _records += LogRecord(level, exception, messageProducer())
    }

    override fun isLoggable(level: LogLevel): Boolean {
        return true
    }

    data class LogRecord(val level: LogLevel, val exception: Throwable?, val message : String)
}

interface ToProxy {

    //Get some variety...
    fun dummy(arg1 : String, arg2 : Int) : Boolean
}

class MemoryImpl(val expectedResult : Boolean? = null, val expectedException: Exception? = null) : ToProxy {

    private val _calls = mutableListOf<Inputs>()
    val calls : List<Inputs>
        get() = _calls

    data class Inputs(val arg1: String, val arg2 : Int)

    override fun dummy(arg1: String, arg2: Int): Boolean {
        _calls += Inputs(arg1, arg2)
        if(expectedResult != null) {
            return expectedResult
        } else {
            throw expectedException!!
        }
    }

}
