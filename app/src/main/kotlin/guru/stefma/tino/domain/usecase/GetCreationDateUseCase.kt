package guru.stefma.tino.domain.usecase

import guru.stefma.tino.store.Store
import javax.inject.Inject

interface GetCreationDate {
    suspend operator fun invoke(uid: String): Long
}

class GetCreationDateUseCase @Inject constructor(
    private val store: Store
) : GetCreationDate {

    override suspend fun invoke(uid: String): Long =
        store.getCreationDate(uid)

}