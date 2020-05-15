package io.paloski.logging

import android.util.Log
import java.util.*

//Why use java.util.logging? To keep this light

actual fun makeNativeTree() : Tree = AndroidLogTree

object AndroidLogTree : Tree {
    override fun log(tag: String, level: LogLevel, messageProducer: () -> String) {
        when(level) {
            LogLevel.INFO ->  Log.i(tag, messageProducer())
            LogLevel.DEBUG -> Log.d(tag, messageProducer())
            LogLevel.WARN -> Log.w(tag, messageProducer())
            LogLevel.ERROR -> Log.e(tag, messageProducer())
            LogLevel.FATAL -> Log.wtf(tag, messageProducer())
        }

    }

    override fun isLoggable(tag : String, level: LogLevel) : Boolean {
        val levelInt : Int = when(level) {
            LogLevel.INFO -> Log.INFO
            LogLevel.DEBUG -> Log.DEBUG
            LogLevel.WARN -> Log.WARN
            LogLevel.ERROR -> Log.ERROR
            LogLevel.FATAL -> Log.ASSERT
        }
        return Log.isLoggable(tag, levelInt)
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