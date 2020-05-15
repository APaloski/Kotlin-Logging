package io.paloski.logging

/**
 * Obtains the default tree for this platform, the "native fauna" if you will
 */
actual fun makeNativeTree(): Tree = ConsoleTree

object ConsoleTree : Tree {
    override fun log(tag: String, level: LogLevel, messageProducer: () -> String) {
        when(level) {
            LogLevel.INFO -> console.log("[INFO] [$tag] ${messageProducer()}")
            LogLevel.DEBUG -> console.log("[DEBUG] [$tag] ${messageProducer()}")
            LogLevel.WARN -> console.warn("[WARN] [$tag] ${messageProducer()}")
            LogLevel.ERROR -> console.error("[ERROR] [$tag] ${messageProducer()}")
            LogLevel.FATAL -> console.error("[FATAL] [$tag] ${messageProducer()}")
        }
    }

}

actual object Forest {
    actual val trees: Set<Tree>
        get() = sneakyTrees
    private val sneakyTrees = mutableSetOf<Tree>()

    actual fun plant(tree: Tree) {
        require(tree != ForestTree)
        sneakyTrees += tree
    }

    actual fun uproot(tree: Tree) {
        require(tree != ForestTree)
        sneakyTrees -= tree
    }

}