package io.paloski.logging

import java.util.*
import java.util.concurrent.ConcurrentSkipListSet
import java.util.logging.Level
import java.util.logging.Logger

//Why use java.util.logging? To keep this light

fun Forest.plantAllServiceTrees() {
    val loader = ServiceLoader.load(Tree::class.java)
    loader.forEach { it.plant() }
}

actual fun makeNativeTree() : Tree = JavaUtilLoggingTree

object JavaUtilLoggingTree : Tree {
    override fun log(tag: String, level: LogLevel, messageProducer: () -> String) {
        Logger.getLogger(tag).log(level.toJULLevel(), messageProducer())
    }

    override fun isLoggable(tag : String, level: LogLevel) =
        Logger.getLogger(tag).isLoggable(level.toJULLevel())
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