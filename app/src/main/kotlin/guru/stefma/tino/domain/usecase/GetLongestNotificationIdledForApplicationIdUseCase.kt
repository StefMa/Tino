package guru.stefma.tino.domain.usecase

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetLongestNotificationIdledForApplicationIdUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : ParamizedUseCase<GetLongestNotificationIdledForApplicationIdUseCase.Params, Single<Long>> {

    override fun invoke(param: Params): Single<Long> {
        return getAllNotifications(GetAllNotificationsUseCase.Params(param.uid))
            .map { it.filter { it.appName == param.appId } }
            .map { it.map { it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp } }
            .map { it.maxBy { it } ?: 0L }
    }

    class Params(val uid: String, val appId: String)

}

typealias GetLongestNotificationIdledForApplicationId = ParamizedUseCase<GetLongestNotificationIdledForApplicationIdUseCase.Params, Single<Long>>

val GetLongestNotificationIdledForApplicationIdClass = ParamizedUseCase::class.java