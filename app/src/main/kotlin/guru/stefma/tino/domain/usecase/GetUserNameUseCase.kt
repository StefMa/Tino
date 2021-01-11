package guru.stefma.tino.domain.usecase

import guru.stefma.tino.store.Store
import javax.inject.Inject

interface GetUserName {
    suspend operator fun invoke(uid: String): String
}

class GetUserNameUseCase @Inject constructor(
    private val store: Store
) : GetUserName {

    override suspend fun invoke(uid: String): String {
        return store.getUserName(uid)
    }

}