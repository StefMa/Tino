package guru.stefma.tino.domain.usecase

import javax.inject.Inject

interface GetLongestNotificationIdled {
    suspend operator fun invoke(uid: String): Pair<String, Long>
}

class GetLongestNotificationIdledUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : GetLongestNotificationIdled {

    override suspend fun invoke(uid: String): Pair<String, Long> {
        return getAllNotifications(uid)
            .map { it.appName to it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
            .maxBy { it.second } ?: "" to 0L
    }
}