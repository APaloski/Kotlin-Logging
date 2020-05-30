package io.paloski.logging

/**
 * Obtains the default tree for this platform, the "native fauna" if you will
 */
actual fun makeNativeTree(): Tree = ConsoleTree

object ConsoleTree : Tree {
    override fun log(tag: String, level: LogLevel, exception : Throwable?, messageProducer: () -> String) {
        when(level) {
            LogLevel.INFO -> {
                console.log("[INFO] [$tag] ${messageProducer()} ${if(exception != null) "v- Stack Below -v" else ""}")
                //console.log doesn't appear to print stack traces, that's fun huh? So we need to use warn! (Or maybe error? Testing to determine this..)
                exception?.apply { console.warn(exception) }
            }
            LogLevel.DEBUG -> {
                console.log("[DEBUG] [$tag] ${messageProducer()} ${if(exception != null) "v- Stack Below -v" else ""}")
                //console.log doesn't appear to print stack traces, that's fun huh? So we need to use warn!
                exception?.apply { console.warn(exception) }
            }
            LogLevel.WARN -> {
                console.warn("[WARN] [$tag] ${messageProducer()} ${if(exception != null) "v- Stack Below -v" else ""}")
                exception?.apply { console.warn(exception) }
            }
            LogLevel.ERROR -> {
                console.error("[ERROR] [$tag] ${messageProducer()} ${if(exception != null) "v- Stack Below -v" else ""}")
                exception?.apply { console.error(exception) }
            }
            LogLevel.FATAL -> {
                console.error("[FATAL] [$tag] ${messageProducer()} ${if(exception != null) "v- Stack Below -v" else ""}")
                exception?.apply { console.error(exception) }
            }
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