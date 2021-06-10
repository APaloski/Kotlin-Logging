package io.paloski.logging

import kotlin.reflect.KClass

expect enum class LogLevel {
    TRACE,
    INFO,
    DEBUG,
    WARN,
    ERROR
    ;
}


inline fun <reified T : Any?> T.log(level : LogLevel, message : String, exception : Throwable? = null) = log(level, exception) { message }
inline fun <reified T : Any?> T.log(level : LogLevel, exception : Throwable? = null, noinline messageProducer : () -> String) = Tree.makeTree<T>().log(level, exception, messageProducer)

inline fun <reified T : Any?> T.traceLog(message: String, exception: Throwable? = null) = log(LogLevel.TRACE, message, exception)
inline fun <reified T : Any?> T.infoLog(message: String, exception : Throwable? = null) = log(LogLevel.INFO, message, exception)
inline fun <reified T : Any?> T.debugLog(message: String, exception : Throwable? = null) = log(LogLevel.DEBUG, message, exception)
inline fun <reified T : Any?> T.warningLog(message: String, exception : Throwable? = null) = log(LogLevel.WARN, message, exception)
inline fun <reified T : Any?> T.errorLog(message: String, exception : Throwable? = null) = log(LogLevel.ERROR, message, exception)

inline fun <reified T : Any?> T.traceLog(exception : Throwable? = null, noinline messageProducer : () -> String) = log(LogLevel.TRACE, exception, messageProducer)
inline fun <reified T : Any?> T.infoLog(exception : Throwable? = null, noinline messageProducer : () -> String) = log(LogLevel.INFO, exception, messageProducer)
inline fun <reified T : Any?> T.debugLog(exception : Throwable? = null,noinline messageProducer : () -> String) = log(LogLevel.DEBUG, exception, messageProducer)
inline fun <reified T : Any?> T.warningLog(exception : Throwable? = null,noinline messageProducer : () -> String) = log(LogLevel.WARN, exception, messageProducer)
inline fun <reified T : Any?> T.errorLog(exception : Throwable? = null,noinline messageProducer : () -> String) = log(LogLevel.ERROR, exception, messageProducer)

/**
 * Represents a single type of logger, usually delegating to an underlying framework for the platform.
 */
interface Tree {

    fun trace(exception: Throwable? = null, messageProducer: () -> String) = log(LogLevel.TRACE, exception, messageProducer)
    fun info(exception : Throwable? = null, messageProducer: () -> String) = log(LogLevel.INFO, exception, messageProducer)
    fun debug(exception : Throwable? = null, messageProducer: () -> String) = log(LogLevel.DEBUG, exception, messageProducer)
    fun warning(exception : Throwable? = null, messageProducer: () -> String) = log(LogLevel.WARN, exception, messageProducer)
    fun error(exception : Throwable? = null, messageProducer: () -> String) = log(LogLevel.ERROR, exception, messageProducer)

    fun log(level: LogLevel, exception : Throwable? = null, messageProducer: () -> String)

    fun isLoggable(level: LogLevel) : Boolean

    companion object {
        fun trace(tag: String, exception: Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.TRACE, exception, messageProducer)
        fun info(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.INFO, exception, messageProducer)
        fun debug(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.DEBUG, exception, messageProducer)
        fun warning(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.WARN, exception, messageProducer)
        fun error(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.ERROR, exception, messageProducer)

        fun log(cls: KClass<*>, level: LogLevel, exception: Throwable?, messageProducer: () -> String) {
            makeTree(cls).log(level, exception, messageProducer)
        }

        fun log(tag: String, level: LogLevel, exception: Throwable?, messageProducer: () -> String) {
            makeTree(tag).log(level, exception, messageProducer)
        }

        fun isLoggable(tag: String, level: LogLevel) {
            makeTree(tag).isLoggable(level)
        }

    }
}

inline fun <reified T> Tree.Companion.makeTree() = makeTree(T::class)
expect fun Tree.Companion.makeTree(cls : KClass<*>) : Tree
expect fun Tree.Companion.makeTree(tag : String) : Tree