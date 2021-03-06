package io.paloski.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

//Must be a class for service loading purposes
class Slf4jTree(val logger : Logger) : Tree {
    override fun log(level: LogLevel, exception : Throwable?, messageProducer: () -> String) {
        if(logger.isLoggable(level)) {
            logger.log(level.toSlf4jLevel(), exception, messageProducer())
        }
    }

    override fun isLoggable(level: LogLevel) = logger.isLoggable(level)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

private fun Logger.log(level: Level, exception: Throwable?, message:  String) {
    when(level) {
        Level.ERROR -> error(message, exception)
        Level.WARN -> warn(message, exception)
        Level.INFO -> info(message, exception)
        Level.DEBUG -> debug(message, exception)
        Level.TRACE -> trace(message, exception)
    }
}

private fun LogLevel.toSlf4jLevel() : Level =
    when(this) {
        LogLevel.TRACE -> Level.TRACE
        LogLevel.INFO -> Level.INFO
        LogLevel.DEBUG -> Level.DEBUG
        LogLevel.WARN -> Level.WARN
        LogLevel.ERROR -> Level.ERROR
    }

private fun Logger.isLoggable(level: LogLevel) : Boolean =
    when(level.toSlf4jLevel()) {
        Level.ERROR -> isErrorEnabled
        Level.WARN -> isWarnEnabled
        Level.INFO -> isInfoEnabled
        Level.DEBUG -> isDebugEnabled
        Level.TRACE -> isTraceEnabled
    }