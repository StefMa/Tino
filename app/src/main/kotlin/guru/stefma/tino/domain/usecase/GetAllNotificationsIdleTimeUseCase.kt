package guru.stefma.tino.domain.usecase

import io.reactivex.Single
import javax.inject.Inject

class GetAllNotificationIdleTimeUseCase @Inject constructor(
    val getAllNotifications: GetAllNotifications
) : ParamizedUseCase<GetAllNotificationIdleTimeUseCase.Params, Single<Long>> {

    override operator fun invoke(param: Params): Single<Long> {
        return getAllNotifications(GetAllNotificationsUseCase.Params(param.uid))
            .map { notifications ->
                notifications.map { it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
            }
            .map { it.sum() }
    }

    class Params(val uid: String)
}

typealias GetAllNotificationsIdleTime = ParamizedUseCase<GetAllNotificationIdleTimeUseCase.Params, Single<Long>>

val GetAllNotificationsIdleTimeClass = ParamizedUseCase::class.java