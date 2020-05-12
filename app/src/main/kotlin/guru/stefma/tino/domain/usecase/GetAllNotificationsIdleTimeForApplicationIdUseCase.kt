package guru.stefma.tino.domain.usecase

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAllNotificationsIdleTimeForApplicationIdUseCase @Inject constructor(
    val getAllNotifications: GetAllNotifications
) : ParamizedUseCase<GetAllNotificationsIdleTimeForApplicationIdUseCase.Params, Single<Long>> {

    override operator fun invoke(param: Params): Single<Long> {
        return getAllNotifications(GetAllNotificationsUseCase.Params(param.uid))
            .map { it.filter { it.appName == param.appId } }
            .map { notifications ->
                notifications.map { it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
            }
            .map { it.sum() }
    }

    class Params(val uid: String, val appId: String)
}

typealias GetAllNotificationsIdleTimeForApplicationId = ParamizedUseCase<GetAllNotificationsIdleTimeForApplicationIdUseCase.Params, Single<Long>>

val GetAllNotificationsIdleTimeForApplicationIdClass = ParamizedUseCase::class.java