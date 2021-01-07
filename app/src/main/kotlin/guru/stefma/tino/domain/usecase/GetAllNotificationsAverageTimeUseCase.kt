package guru.stefma.tino.domain.usecase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetAllNotificationsAverageTime {
    operator fun invoke(uid: String): Flow<Long>
}

class GetAllNotificationsAverageTimeUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : GetAllNotificationsAverageTime {

    override fun invoke(uid: String): Flow<Long> {
        return flow {
            while (true) {
                val notifications = getAllNotifications(uid)
                if (notifications.isEmpty()) {
                    emit(0L)
                } else {
                    val notificationTime = notifications
                        .map { it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
                        .sum()

                    emit(notificationTime / notifications.size)
                }

                delay(5000)
            }
        }
    }
}