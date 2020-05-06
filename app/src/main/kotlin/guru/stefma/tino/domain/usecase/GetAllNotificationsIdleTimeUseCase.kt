package guru.stefma.tino.domain.usecase

import io.reactivex.Single
import javax.inject.Inject

class GetAllNotificationsIdleTimeUseCase @Inject constructor(
    val getAllNotifications: GetAllNotifications
) : GetAllNotificationsIdleTime {

    override operator fun invoke(param: Params): Single<Long> {
        return getAllNotifications(GetAllNotificationsUseCase.Params(param.uid))
            .map { notifications ->
                notifications.map { it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
            }
            .map { it.sum() }
    }

    class Params(val uid: String)
}

typealias GetAllNotificationsIdleTime = ParamizedUseCase<GetAllNotificationsIdleTimeUseCase.Params, Single<Long>>