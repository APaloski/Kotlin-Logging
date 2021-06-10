package io.paloski.logging

import kotlin.reflect.KClass

actual enum class LogLevel {
    TRACE,
    INFO,
    DEBUG,
    WARN,
    ERROR
    ;

    fun asLogString(): String =
        "[$name]"
}


actual fun Tree.Companion.makeTree(tag: String): Tree =
    ConsoleTree(tag)

actual fun Tree.Companion.makeTree(cls : KClass<*>) : Tree =
    ConsoleTree(cls.simpleName.orEmpty())

//The "println equivalent", but it works for now at a basic level
class ConsoleTree(val tag : String) : Tree {
    override fun log(level: LogLevel, exception: Throwable?, messageProducer: () -> String) {
        when (level) {
            LogLevel.TRACE -> {
                console.error("[TRACE] [$tag] ${messageProducer()} ${if (exception != null) "v- Stack Below -v" else ""}")
                exception?.apply { console.error(exception) }
            }
            LogLevel.INFO -> {
                console.log("[INFO] [$tag] ${messageProducer()} ${if (exception != null) "v- Stack Below -v" else ""}")
                //console.log doesn't appear to print stack traces, that's fun huh? So we need to use warn! (Or maybe error? Testing to determine this..)
                exception?.apply { console.warn(exception) }
            }
            LogLevel.DEBUG -> {
                console.log("[DEBUG] [$tag] ${messageProducer()} ${if (exception != null) "v- Stack Below -v" else ""}")
                //console.log doesn't appear to print stack traces, that's fun huh? So we need to use warn!
                exception?.apply { console.warn(exception) }
            }
            LogLevel.WARN -> {
                console.warn("[WARN] [$tag] ${messageProducer()} ${if (exception != null) "v- Stack Below -v" else ""}")
                exception?.apply { console.warn(exception) }
            }
            LogLevel.ERROR -> {
                console.error("[ERROR] [$tag] ${messageProducer()} ${if (exception != null) "v- Stack Below -v" else ""}")
                exception?.apply { console.error(exception) }
            }
        }
    }

    override fun isLoggable(level: LogLevel): Boolean = true /* We don't do any filtering here */

}