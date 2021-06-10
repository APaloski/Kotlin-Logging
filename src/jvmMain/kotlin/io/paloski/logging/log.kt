package io.paloski.logging

import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass

actual enum class LogLevel(private val level : Level) {
    TRACE(Level.TRACE),
    INFO(Level.INFO),
    DEBUG(Level.DEBUG),
    WARN(Level.WARN),
    ERROR(Level.ERROR)
    ;
}

/**
 * Creates a proxy of the given object that will log all function calls going into it using the forest.
 *
 * This is subject to all the restrictions imposed by [Proxy.newProxyInstance]
 */
inline fun <reified T : Any> T.logged(callTracingLevel: LogLevel = LogLevel.INFO,
                                      errorLevel: LogLevel = LogLevel.ERROR,
                                       treeFactory : (KClass<T>) -> Tree = { Tree.makeTree(it) }) : T =
    Proxy.newProxyInstance(this::class.java.classLoader, arrayOf(T::class.java), LoggingInvocationHandler(this, treeFactory(T::class), callTracingLevel, errorLevel)) as T

//Gets around internal classes exposed
fun <T : Any> LoggingInvocationHandler(actual : T, tree: Tree, callTracingLevel : LogLevel, errorLevel: LogLevel): InvocationHandler
    = LoggingInvocationHandlerImpl(actual, tree, callTracingLevel, errorLevel)

private class LoggingInvocationHandlerImpl<T : Any>(val actual : T, val tree: Tree, val callTracingLevel : LogLevel, val errorLevel: LogLevel) : InvocationHandler {

    private val callIdIncrementer = AtomicLong()

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        requireNotNull(proxy)
        requireNotNull(method)
        requireNotNull(args)
        val callId = callIdIncrementer.getAndIncrement()
        val formattedMethodName = "${actual}.${method.name}(${args.joinToString(", ") { it.toString() }})"
        return try {
            tree.log(level = callTracingLevel) {
                "==> {CallId=${callId}} Calling $formattedMethodName"
            }
            val result = method.invoke(actual, *args)
            tree.log(level = callTracingLevel) {
                "<== {CallId=${callId}} Returning $result from $formattedMethodName"
            }
            result
        } catch (exp : InvocationTargetException) {
            tree.log(level = errorLevel, exception = exp.cause ?: exp) {
                "<== {CallId=${callId}} EXCEPTIONAL RESULT from $formattedMethodName"
            }
            //We should be able to get the cause here based on method.invoke, but fall back to the exp itself so the stack isn't lost
            throw exp.cause ?: exp
        }
    }
}

actual fun Tree.Companion.makeTree(tag: String): Tree =
    Slf4jTree(LoggerFactory.getLogger(tag))

actual fun Tree.Companion.makeTree(cls : KClass<*>) : Tree =
    Slf4jTree(LoggerFactory.getLogger(cls.java))