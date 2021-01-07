package guru.stefma.tino.domain.usecase

import guru.stefma.tino.authentication.Authentication
import guru.stefma.tino.store.Store
import guru.stefma.tino.store.UserWithUidDoesNotExist
import javax.inject.Inject

interface InitialSetup {
    suspend operator fun invoke(): Boolean
}

class InitialSetupUseCase @Inject constructor(
    private val authentication: Authentication,
    private val store: Store,
    private val generateUserNameAndStore: GenerateUserNameAndStore
) : InitialSetup {

    override suspend fun invoke(): Boolean = try {
        authentication.createAnonymousUser()
        generateAndStoreUserName()
        setCreationDate()
    } catch (err: Throwable) {
        false
    }

    private suspend fun generateAndStoreUserName() {
        if (!doesUserAlreadyExist()) generateUserNameAndStore()
    }

    private suspend fun doesUserAlreadyExist(): Boolean =
        try {
            store.getUserName(authentication.uid).isNotEmpty()
        } catch (userDoesNotExist: UserWithUidDoesNotExist) {
            false
        }

    private suspend fun setCreationDate(): Boolean =
        try {
            if (store.getCreationDate(authentication.uid) == 0L) {
                val unixtimestamp = System.currentTimeMillis() / 1000
                store.setCreationDate(authentication.uid, unixtimestamp)
            }
            true
        } catch (userDoesNotExist: UserWithUidDoesNotExist) {
            false
        }
}