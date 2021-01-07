package guru.stefma.tino.domain.usecase

import javax.inject.Inject

interface GetLongestNotificationIdledForApplicationId {
    suspend operator fun invoke(uid: String, appId: String): Long
}

class GetLongestNotificationIdledForApplicationIdUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : GetLongestNotificationIdledForApplicationId {

    override suspend fun invoke(uid: String, appId: String): Long {
        return getAllNotifications(uid)
            .filter { it.appName == appId }
            .map { it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
            .maxBy { it } ?: 0L
    }

}