package guru.stefma.tino.domain.usecase

import guru.stefma.tino.authentication.Authentication
import guru.stefma.tino.namegenerator.NameGenerator
import guru.stefma.tino.store.Store
import guru.stefma.tino.store.UserNameAlreadyTakenException
import java.util.*
import javax.inject.Inject

class GenerateUserNameAndStoreUseCase @Inject constructor(
    private val store: Store,
    private val nameGenerator: NameGenerator,
    private val authentication: Authentication
) : SuspendingUseCase<Unit> {

    private val maxRetryCount = 5
    private var retryCount = 0

    override suspend fun invoke() = generateNameAndSave()

    private suspend fun generateNameAndSave() {
        var name = nameGenerator.generateName()
        retryCount += 1
        if (retryCount > maxRetryCount) {
            name += UUID.randomUUID().leastSignificantBits
        }
        try {
            store.setUserName(authentication.uid, name)
        } catch (usernameTaken: UserNameAlreadyTakenException) {
            generateNameAndSave()
        }
    }
}

typealias GenerateUserNameAndStore = SuspendingUseCase<Unit>

val GenerateUserNameAndStoreClass = SuspendingUseCase::class.java
