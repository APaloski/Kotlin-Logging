package io.paloski.logging

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

inline fun <reified T : Any?> T.log(level : LogLevel, message : String, exception : Throwable? = null) = log(level, exception) { message }
inline fun <reified T : Any?> T.log(level : LogLevel, exception : Throwable? = null, noinline messageProducer : () -> String) = Forest.log(T::class.simpleName.orEmpty(), level, exception, messageProducer)

inline fun <reified T : Any?> T.infoLog(message: String, exception : Throwable? = null) = log(LogLevel.INFO, message, exception)
inline fun <reified T : Any?> T.debugLog(message: String, exception : Throwable? = null) = log(LogLevel.DEBUG, message, exception)
inline fun <reified T : Any?> T.warningLog(message: String, exception : Throwable? = null) = log(LogLevel.WARN, message, exception)
inline fun <reified T : Any?> T.errorLog(message: String, exception : Throwable? = null) = log(LogLevel.ERROR, message, exception)
inline fun <reified T : Any?> T.fatalLog(message: String, exception : Throwable? = null) = log(LogLevel.FATAL, message, exception)

inline fun <reified T : Any?> T.infoLog(exception : Throwable? = null, noinline messageProducer : () -> String) = log(LogLevel.INFO, exception, messageProducer)
inline fun <reified T : Any?> T.debugLog(exception : Throwable? = null,noinline messageProducer : () -> String) = log(LogLevel.DEBUG, exception, messageProducer)
inline fun <reified T : Any?> T.warningLog(exception : Throwable? = null,noinline messageProducer : () -> String) = log(LogLevel.WARN, exception, messageProducer)
inline fun <reified T : Any?> T.errorLog(exception : Throwable? = null,noinline messageProducer : () -> String) = log(LogLevel.ERROR, exception, messageProducer)
inline fun <reified T : Any?> T.fatalLog(exception : Throwable? = null,noinline messageProducer : () -> String) = log(LogLevel.FATAL, exception, messageProducer)

/**
 * Represents a single type of logger, usually delegating to an underlying framework for the platform. Multiple of these
 * can be installed in the [Forest] by using the [plant] method.
 */
interface Tree {

    fun info(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.INFO, exception, messageProducer)
    fun debug(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.DEBUG, exception, messageProducer)
    fun warning(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.WARN, exception, messageProducer)
    fun error(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.ERROR, exception, messageProducer)
    fun fatal(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.FATAL, exception, messageProducer)

    fun log(tag : String, level: LogLevel, exception : Throwable? = null, messageProducer: () -> String)

    fun isLoggable(tag : String, level: LogLevel) = true
}

/**
 * Plants this tree, adding it to the forest to receive all logging calls
 */
fun Tree.plant() = Forest.plant(this)

/**
 * Uproots this tree, removing it from the forest and stopping log entries
 */
fun Tree.uproot() = Forest.uproot(this)

/**
 * Defines the "Forest" of trees, that is all of the places we are going to be writing out log entires to when one of the
 * global log methods like [debugLog] is called.
 *
 * To use the basic tree for the dependency with this forest call [plantDefaultTree], some platforms may support loading
 * all types of trees with similar methods.
 */
expect object Forest {

    val trees : Set<Tree>

    fun plant(tree: Tree)

    fun uproot(tree : Tree)

}

/**
 * Plants the default, platform defined tree into the forest.
 */
expect fun Forest.plantDefaultTree()

fun Forest.info(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.INFO, exception, messageProducer)
fun Forest.debug(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.DEBUG, exception, messageProducer)
fun Forest.warning(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.WARN, exception, messageProducer)
fun Forest.error(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.ERROR, exception, messageProducer)
fun Forest.fatal(tag: String, exception : Throwable? = null, messageProducer: () -> String) = log(tag, LogLevel.FATAL, exception, messageProducer)

fun Forest.log(tag : String, level: LogLevel, exception : Throwable? = null, messageProducer: () -> String) = Forest.asTree().log(tag, level, exception, messageProducer)

fun Forest.asTree() : Tree = ForestTree

internal object ForestTree : Tree {
    override fun log(tag: String, level: LogLevel, exception : Throwable?, messageProducer: () -> String) {
        Forest.trees.onEach { tree -> tree.log(tag, level, exception, messageProducer) }
    }

    override fun isLoggable(tag: String, level: LogLevel): Boolean {
        return Forest.trees.any { tree -> tree.isLoggable(tag, level) }
    }
}