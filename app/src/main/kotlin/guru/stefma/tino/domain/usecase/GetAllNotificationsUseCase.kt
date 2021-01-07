package guru.stefma.tino.domain.usecase

import guru.stefma.tino.store.Store
import javax.inject.Inject

interface GetAllNotifications {
    suspend operator fun invoke(uid: String): List<Store.Data>
}

class GetAllNotificationsUseCase @Inject constructor(
    private val store: Store
) : GetAllNotifications {

    override suspend fun invoke(uid: String): List<Store.Data> =
        store.getAll(uid)

}