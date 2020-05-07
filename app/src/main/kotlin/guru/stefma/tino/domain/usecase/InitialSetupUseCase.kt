package guru.stefma.tino.domain.usecase

import guru.stefma.tino.authentication.Authentication
import guru.stefma.tino.store.Store
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle
import javax.inject.Inject

class InitialSetupUseCase @Inject constructor(
    private val authentication: Authentication,
    private val store: Store,
    private val generateUserNameAndStore: GenerateUserNameAndStore
) : InitialSetup {

    override fun invoke(): Single<Boolean> =
        rxSingle { authentication.createAnonymousUser() }
            .flatMap { generateAndStoreUserName() }
            .flatMap { setCreationDate() }
            .map { true }
            .onErrorReturn { false }

    private fun generateAndStoreUserName(): Single<Unit> =
        rxSingle { store.getUserName(authentication.uid).isNotEmpty() }
            .onErrorReturn { false }
            .flatMap { usernameAvailable ->
                if (usernameAvailable) {
                    Single.just(Unit)
                } else {
                    rxSingle { generateUserNameAndStore() }
                }
            }

    private fun setCreationDate(): Single<Unit> =
        rxSingle { store.getCreationDate(authentication.uid) != 0L }
            .onErrorReturn { false }
            .flatMap { creationDateAvailable ->
                if (creationDateAvailable) {
                    Single.just(Unit)
                } else {
                    val unixtimestamp = System.currentTimeMillis() / 1000
                    rxSingle { store.setCreationDate(authentication.uid, unixtimestamp) }
                }
            }

}

typealias InitialSetup = UseCase<Single<Boolean>>