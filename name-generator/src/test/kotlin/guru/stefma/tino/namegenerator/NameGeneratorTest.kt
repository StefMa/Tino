package guru.stefma.tino.namegenerator

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class NameGeneratorTest {

    @Test
    fun `name generator should generate name correctly`() {
        val name = runBlocking { NameGenerator().generateName() }

        assertThat(name).isNotNull()
        val fullName = name.split(" ")
        assertThat(fullName).hasSize(2)
        assertThat(fullName.first().isUpperCase()).isTrue()
        assertThat(fullName.last().isUpperCase()).isTrue()
    }

    @Test
    fun `generate names two times should not return same name`() {
        val firstName = runBlocking { NameGenerator().generateName() }
        val secondName = runBlocking { NameGenerator().generateName() }

        assertThat(firstName).isNotEqualTo(secondName)
    }
}

private fun String.isUpperCase() = this[0].isUpperCase()