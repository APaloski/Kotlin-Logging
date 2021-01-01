package io.paloski.logging

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import java.util.concurrent.atomic.AtomicLong

fun Forest.plantAllServiceTrees() {
    serviceLoadAllTrees().forEach { it.plant() }
}

private fun serviceLoadAllTrees() = ServiceLoader.load(Tree::class.java).toSet()

actual fun Forest.plantDefaultTree() {
    val trees = serviceLoadAllTrees()
    assert(trees.size == 1) {
        "Exactly one tree must be service loaded at runtime to use plantDefaultTree (Found: $trees). Use Forest.plantAllServiceTrees or manually plant your trees instead"
    }
    trees.first().plant()
}

/**
 * Creates a proxy of the given object that will log all function calls going into it using the forest.
 *
 * This is subject to all the restrictions imposed by [Proxy.newProxyInstance]
 */
inline fun <reified T : Any> T.logged(callTracingLevel: LogLevel = LogLevel.INFO,
                                      errorLevel: LogLevel = LogLevel.ERROR) : T =
    Proxy.newProxyInstance(this::class.java.classLoader, arrayOf(T::class.java), LoggingInvocationHandler(this, callTracingLevel, errorLevel)) as T

//Gets around internal classes exposed
fun <T : Any> LoggingInvocationHandler(actual : T, callTracingLevel : LogLevel, errorLevel: LogLevel): InvocationHandler
    = LoggingInvocationHandlerImpl(actual, callTracingLevel, errorLevel)

private class LoggingInvocationHandlerImpl<T : Any>(val actual : T, val callTracingLevel : LogLevel, val errorLevel: LogLevel) : InvocationHandler {

    private val callIdIncrementer = AtomicLong()

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        requireNotNull(proxy)
        requireNotNull(method)
        requireNotNull(args)
        val callId = callIdIncrementer.getAndIncrement()
        val formattedMethodName = "${actual}.${method.name}(${args.joinToString(", ") { it.toString() }})"
        return try {
            Forest.log(actual::class.simpleName.orEmpty(), level = callTracingLevel) {
                "==> {CallId=${callId}} Calling $formattedMethodName"
            }
            val result = method.invoke(actual, args)
            Forest.log(actual::class.simpleName.orEmpty(), level = callTracingLevel) {
                "<== {CallId=${callId}} Returning $result from $formattedMethodName"
            }
            result
        } catch (exp : Exception) {
            Forest.log(actual::class.simpleName.orEmpty(), level = errorLevel, exception = exp) {
                "<== {CallId=${callId}} EXCEPTIONAL RESULT from $formattedMethodName"
            }
            throw exp
        }
    }
}

actual object Forest {
    actual val trees: Set<Tree>
        get() = sneakyTrees

    private val sneakyTrees = Collections.synchronizedSet<Tree>(mutableSetOf())

    actual fun plant(tree: Tree) {
        require(tree != ForestTree)
        sneakyTrees += tree
    }

    actual fun uproot(tree: Tree) {
        require(tree != ForestTree)
        sneakyTrees -= tree
    }
}