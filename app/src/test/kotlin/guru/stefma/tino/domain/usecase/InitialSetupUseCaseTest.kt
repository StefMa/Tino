package guru.stefma.tino.domain.usecase

import com.google.common.truth.Truth.assertThat
import guru.stefma.tino.mocks.MockAuthentication
import guru.stefma.tino.mocks.MockStore
import guru.stefma.tino.store.UID
import guru.stefma.tino.store.UserName
import guru.stefma.tino.store.UserWithUidDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class InitialSetupUseCaseTest {

    private val authentication = MockAuthentication()

    private val store = MockStore()

    private val generateUserNameAndSave = object : GenerateUserNameAndStore {
        var called = false
        override suspend fun invoke() {
            called = true
        }
    }

    @Test
    fun `test usecase on success`() {
        val useCase = InitialSetupUseCase(authentication, store, generateUserNameAndSave)

        val initSuccessfully = runBlocking { useCase.invoke() }

        assertThat(initSuccessfully).isTrue()
    }

    @Test
    fun `test usecase on create users failure`() {
        val authentication = object : MockAuthentication() {
            override val uid: String = ""

            override suspend fun createAnonymousUser(): Unit = throw Error()
        }
        val useCase = InitialSetupUseCase(authentication, store, generateUserNameAndSave)

        val initSuccessfully = runBlocking { useCase.invoke() }

        assertThat(initSuccessfully).isFalse()
    }

    @Test
    fun `test usecase on getUserName failure`() {
        val store = object : MockStore() {
            override suspend fun getUserName(uid: UID): UserName {
                throw UserWithUidDoesNotExist(uid)
            }
        }
        val useCase = InitialSetupUseCase(authentication, store, generateUserNameAndSave)

        val initSuccessfully = runBlocking { useCase.invoke() }

        assertThat(initSuccessfully).isTrue()
        assertThat(generateUserNameAndSave.called).isTrue()
    }

    @Test
    fun `test usecase on getUserName success should not call generate name`() {
        val store = object : MockStore() {
            override suspend fun getUserName(uid: UID): UserName = "SuperSweetName"
        }
        val useCase = InitialSetupUseCase(authentication, store, generateUserNameAndSave)

        runBlocking { useCase.invoke() }

        assertThat(generateUserNameAndSave.called).isFalse()
    }

    @Test
    fun `test usecase on named generator failure`() {
        val generateUserNameAndSave = object : GenerateUserNameAndStore {
            override suspend fun invoke() = throw Error()
        }
        val useCase = InitialSetupUseCase(authentication, store, generateUserNameAndSave)

        val initSuccessfully = runBlocking { useCase.invoke() }

        assertThat(initSuccessfully).isFalse()
    }

    @Test
    fun `test usecase on getCreationDate failure`() {
        val store = object : MockStore() {
            override suspend fun getCreationDate(uid: UID): Long {
                throw UserWithUidDoesNotExist(uid)
            }
        }
        val useCase = InitialSetupUseCase(authentication, store, generateUserNameAndSave)

        val initSuccessfully = runBlocking { useCase.invoke() }

        assertThat(initSuccessfully).isFalse()
    }

}
