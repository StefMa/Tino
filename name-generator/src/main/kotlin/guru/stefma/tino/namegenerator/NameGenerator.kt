package guru.stefma.tino.namegenerator

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

interface NameGenerator {

    companion object {
        operator fun invoke(): NameGenerator = DefaultNameGenerator
    }

    suspend fun generateName(): String
}

private object DefaultNameGenerator : NameGenerator {

    private val cachedFirstNames = mutableListOf<String>()
    private val cachedLastName = mutableListOf<String>()

    override suspend fun generateName(): String = suspendCoroutine {
        if (cachedFirstNames.isEmpty() || cachedLastName.isEmpty()) loadNames()

        val firstName = cachedFirstNames[Random.nextInt(cachedFirstNames.size)]
        val lastName = cachedLastName[Random.nextInt(cachedLastName.size)]

        it.resume("$firstName $lastName")
    }

    private fun loadNames() {
        cachedFirstNames.addAll(firstFemale.toNameList())
        cachedFirstNames.addAll(firstMale.toNameList())
        cachedLastName.addAll(last.toNameList())
    }

    private fun String.toNameList(): List<String> =
        split('\n').map { it.capitalize() }

}