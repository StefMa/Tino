package guru.stefma.tino.domain.usecase

import javax.inject.Inject

interface GetAppWithMostNotifications {
    suspend operator fun invoke(uid: String): Pair<String, Long>
}

class GetAppWithMostNotificationsUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : GetAppWithMostNotifications {

    override suspend fun invoke(uid: String): Pair<String, Long> {
        return getAllNotifications(uid)
            .map { it.appName to it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
            .groupBy { it.first }
            .map { it.key to it.value.size.toLong() }
            .maxBy { it.second }
            ?: "" to 0L
    }
}