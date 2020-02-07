package guru.stefma.tino.mocks

import guru.stefma.tino.namegenerator.NameGenerator

open class MockNameGenerator : NameGenerator {
    override suspend fun generateName(): String = ""
}