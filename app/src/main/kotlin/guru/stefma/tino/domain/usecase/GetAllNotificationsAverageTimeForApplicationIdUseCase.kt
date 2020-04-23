package guru.stefma.tino.domain.usecase

import io.reactivex.Single

class GetAllNotificationsAverageTimeForApplicationIdUseCase(
    private val getAllNotifications: GetAllNotifications
) : ParamizedUseCase<GetAllNotificationsAverageTimeForApplicationIdUseCase.Params, Single<Long>> {

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

val GetAllNotificationsAverageTimeForApplicationIdClass = ParamizedUseCase::class.java