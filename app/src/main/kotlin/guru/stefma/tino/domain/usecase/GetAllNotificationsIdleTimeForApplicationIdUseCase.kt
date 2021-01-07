package guru.stefma.tino.domain.usecase

import javax.inject.Inject

interface GetAllNotificationsIdleTimeForApplicationId {
    suspend operator fun invoke(uid: String, appId: String): Long
}

class GetAllNotificationsIdleTimeForApplicationIdUseCase @Inject constructor(
    val getAllNotifications: GetAllNotifications
) : GetAllNotificationsIdleTimeForApplicationId {

    override suspend operator fun invoke(uid: String, appId: String): Long {
        return getAllNotifications(uid)
            .filter { it.appName == appId }
            .map { it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
            .sum()
    }

}