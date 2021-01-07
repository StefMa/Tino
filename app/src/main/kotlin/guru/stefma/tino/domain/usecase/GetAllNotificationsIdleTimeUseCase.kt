package guru.stefma.tino.domain.usecase

import javax.inject.Inject

interface GetAllNotificationsIdleTime {
    suspend operator fun invoke(uid: String): Long
}

class GetAllNotificationsIdleTimeUseCase @Inject constructor(
    val getAllNotifications: GetAllNotifications
) : GetAllNotificationsIdleTime {

    override suspend fun invoke(uid: String): Long {
        return getAllNotifications(uid)
            .map { it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
            .sum()
    }

}