package guru.stefma.tino.domain.usecase

import androidx.core.util.rangeTo
import com.google.common.truth.Truth.assertThat
import guru.stefma.tino.mocks.MockAuthentication
import guru.stefma.tino.mocks.MockNameGenerator
import guru.stefma.tino.mocks.MockStore
import guru.stefma.tino.store.UID
import guru.stefma.tino.store.UserName
import guru.stefma.tino.store.UserNameAlreadyTakenException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.*

class GenerateUserNameAndStoreUseCaseTest {

    @Test
    fun `test usecase should generate name as long as exception from setUserName is thrown`() {
        val mockStore = object : MockStore() {
            var setNameCalled = -1
            override suspend fun setUserName(uid: UID, username: UserName) {
                setNameCalled += 1
                when (setNameCalled) {
                    0,
                    1 -> throw UserNameAlreadyTakenException(username)
                    2 -> return
                }
            }
        }
        val mockNameGenerator = object : MockNameGenerator() {
            var generateNameCalled = 0
            override suspend fun generateName(): String {
                generateNameCalled += 1
                return UUID.randomUUID().toString()
            }
        }
        val useCase = GenerateUserNameAndStoreUseCase(
            mockStore,
            mockNameGenerator,
            MockAuthentication()
        )

        runBlocking { useCase.invoke() }

        assertThat(mockStore.setNameCalled).isEqualTo(2)
        assertThat(mockNameGenerator.generateNameCalled).isEqualTo(3)
    }

    @Test
    fun `test usecase should generate name as long with UUID after 5 times failed`() {
        val mockStore = object : MockStore() {
            var setNameCalled = -1
            var lastSavedUserName = ""
            override suspend fun setUserName(uid: UID, username: UserName) {
                setNameCalled += 1
                lastSavedUserName = username
                if(setNameCalled < 5) throw UserNameAlreadyTakenException(username)
            }
        }
        val mockNameGenerator = object : MockNameGenerator() {
            var generateNameCalled = 0
            val userName = "StefMa"
            override suspend fun generateName(): String {
                generateNameCalled += 1
                return userName
            }
        }
        val useCase = GenerateUserNameAndStoreUseCase(
            mockStore,
            mockNameGenerator,
            MockAuthentication()
        )

        runBlocking { useCase.invoke() }

        assertThat(mockStore.setNameCalled).isEqualTo(5)
        assertThat(mockNameGenerator.generateNameCalled).isEqualTo(6)
        assertThat(mockStore.lastSavedUserName).contains("StefMa-")
    }
}