package guru.stefma.tino.domain.usecase

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAllNotificationsAverageTimeForApplicationIdUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : GetAllNotificationsAverageTimeForApplicationId {

    override fun invoke(param: Params): Single<Long> {
        return getAllNotifications(GetAllNotificationsUseCase.Params(param.uid))
            .map { it.filter { it.appName == param.appId } }
            .map { notifications ->
                if (notifications.isEmpty()) return@map 0L

                val notificationTime =
                    notifications
                        .map { it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
                        .sum()

                notificationTime / notifications.size
            }
    }

    class Params(val uid: String, val appId: String)

}

typealias GetAllNotificationsAverageTimeForApplicationId = ParamizedUseCase<GetAllNotificationsAverageTimeForApplicationIdUseCase.Params, Single<Long>>