package io.paloski.logging

//Allows us to do our service loading safely (hopefully... )
class TestTree : Tree {
    override fun log(tag: String, level: LogLevel, exception: Throwable?, messageProducer: () -> String) = Unit
}