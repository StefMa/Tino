package guru.stefma.tino.domain.usecase

import javax.inject.Inject

interface GetAllNotificationsCount {
    suspend operator fun invoke(uid: String): Int
}

class GetAllNotificationsCountUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : GetAllNotificationsCount {

    override suspend fun invoke(uid: String): Int {
        return getAllNotifications(uid).count()
    }
}