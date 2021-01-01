package io.paloski.logging

import java.util.logging.Level
import java.util.logging.Logger


//Must be a class for service loading purposes - make sure we have equals/hashcode for unique purposes
class JavaUtilLoggingTree : Tree {
    override fun log(tag: String, level: LogLevel, exception : Throwable?, messageProducer: () -> String) {
        if(isLoggable(tag, level)) {
            Logger.getLogger(tag).log(level.toJULLevel(), messageProducer(), exception)
        }
    }

    override fun isLoggable(tag : String, level: LogLevel) =
        Logger.getLogger(tag).isLoggable(level.toJULLevel())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }


}

private sealed class InternalLevels(level : LogLevel) : Level(level.name, level.ordinal) {

    object INFO : InternalLevels(LogLevel.INFO)
    object DEBUG : InternalLevels(LogLevel.DEBUG)
    object WARN : InternalLevels(LogLevel.WARN)
    object ERROR : InternalLevels(LogLevel.ERROR)
    object FATAL : InternalLevels(LogLevel.FATAL)
}

private fun LogLevel.toJULLevel() : Level =
    when(this) {
        LogLevel.INFO -> InternalLevels.INFO
        LogLevel.DEBUG -> InternalLevels.DEBUG
        LogLevel.WARN -> InternalLevels.WARN
        LogLevel.ERROR -> InternalLevels.ERROR
        LogLevel.FATAL -> InternalLevels.FATAL
    }