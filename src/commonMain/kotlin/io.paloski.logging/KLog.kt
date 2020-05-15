package io.paloski.logging

import kotlin.reflect.KClass

enum class LogLevel {
    INFO,
    DEBUG,
    WARN,
    ERROR,
    FATAL
;

    fun asLogString(): String =
        "[$name]"
}

inline fun <reified T : Any?> T.log(level : LogLevel, message : String) = log(level) { message }
inline fun <reified T : Any?> T.log(level : LogLevel, noinline messageProducer : () -> String) = Forest.log(T::class.simpleName.orEmpty(), level, messageProducer)

inline fun <reified T : Any?> T.infoLog(message: String) = log(LogLevel.INFO, message)
inline fun <reified T : Any?> T.debugLog(message: String) = log(LogLevel.DEBUG, message)
inline fun <reified T : Any?> T.warningLog(message: String) = log(LogLevel.WARN, message)
inline fun <reified T : Any?> T.errorLog(message: String) = log(LogLevel.ERROR, message)
inline fun <reified T : Any?> T.fatalLog(message: String) = log(LogLevel.FATAL, message)

inline fun <reified T : Any?> T.infoLog(noinline messageProducer : () -> String) = log(LogLevel.INFO, messageProducer)
inline fun <reified T : Any?> T.debugLog(noinline messageProducer : () -> String) = log(LogLevel.DEBUG, messageProducer)
inline fun <reified T : Any?> T.warningLog(noinline messageProducer : () -> String) = log(LogLevel.WARN, messageProducer)
inline fun <reified T : Any?> T.errorLog(noinline messageProducer : () -> String) = log(LogLevel.ERROR, messageProducer)
inline fun <reified T : Any?> T.fatalLog(noinline messageProducer : () -> String) = log(LogLevel.FATAL, messageProducer)


interface Tree {

    fun info(tag: String, messageProducer: () -> String) = log(tag, LogLevel.INFO, messageProducer)
    fun debug(tag: String, messageProducer: () -> String) = log(tag, LogLevel.DEBUG, messageProducer)
    fun warning(tag: String, messageProducer: () -> String) = log(tag, LogLevel.WARN, messageProducer)
    fun error(tag: String, messageProducer: () -> String) = log(tag, LogLevel.ERROR, messageProducer)
    fun fatal(tag: String, messageProducer: () -> String) = log(tag, LogLevel.FATAL, messageProducer)

    fun log(tag : String, level: LogLevel, messageProducer: () -> String)

    fun isLoggable(tag : String, level: LogLevel) = true
}

/**
 * Obtains the default tree for this platform, the "native fauna" if you will
 */
expect fun makeNativeTree() : Tree

/**
 * Plants this tree,
 */
fun Tree.plant() = Forest.plant(this)

fun Tree.uproot() = Forest.uproot(this)

expect object Forest {

    val trees : Set<Tree>

    fun plant(tree: Tree)

    fun uproot(tree : Tree)

}

fun Forest.info(tag: String, messageProducer: () -> String) = log(tag, LogLevel.INFO, messageProducer)
fun Forest.debug(tag: String, messageProducer: () -> String) = log(tag, LogLevel.DEBUG, messageProducer)
fun Forest.warning(tag: String, messageProducer: () -> String) = log(tag, LogLevel.WARN, messageProducer)
fun Forest.error(tag: String, messageProducer: () -> String) = log(tag, LogLevel.ERROR, messageProducer)
fun Forest.fatal(tag: String, messageProducer: () -> String) = log(tag, LogLevel.FATAL, messageProducer)

fun Forest.log(tag : String, level: LogLevel, messageProducer: () -> String) = Forest.asTree().log(tag, level, messageProducer)

fun Forest.asTree() : Tree = ForestTree

internal object ForestTree : Tree {
    override fun log(tag: String, level: LogLevel, messageProducer: () -> String) {
        Forest.trees.onEach { tree -> tree.log(tag, level, messageProducer) }
    }

    override fun isLoggable(tag: String, level: LogLevel): Boolean {
        return Forest.trees.any { tree -> tree.isLoggable(tag, level) }
    }
}