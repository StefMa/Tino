package guru.stefma.tino.domain.usecase

import javax.inject.Inject

interface GetAllNotificationsAverageTimeForApplicationId {
    suspend operator fun invoke(uid: String, appId: String): Long
}

class GetAllNotificationsAverageTimeForApplicationIdUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : GetAllNotificationsAverageTimeForApplicationId {

    override suspend fun invoke(uid: String, appId: String): Long {
        return getAllNotifications(uid)
            .filter { it.appName == appId }
            .ifEmpty { return 0L }
            .run {
                val notificationTime = this
                    .map { it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
                    .sum()

                notificationTime / size
            }
    }

}