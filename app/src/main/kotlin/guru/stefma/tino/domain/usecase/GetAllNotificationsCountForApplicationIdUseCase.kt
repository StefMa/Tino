package guru.stefma.tino.domain.usecase

import javax.inject.Inject

interface GetAllNotificationsCountForApplicationId {
    suspend operator fun invoke(uid: String, appId: String): Int
}

class GetAllNotificationsCountForApplicationIdUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : GetAllNotificationsCountForApplicationId {

    override suspend fun invoke(uid: String, appId: String): Int {
        return getAllNotifications(uid)
            .filter { it.appName == appId }
            .count()
    }


}