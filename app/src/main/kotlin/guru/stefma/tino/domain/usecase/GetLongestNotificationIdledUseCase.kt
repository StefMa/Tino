package guru.stefma.tino.domain.usecase

import io.reactivex.Single
import javax.inject.Inject

class GetLongestNotificationIdledUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : ParamizedUseCase<GetLongestNotificationIdledUseCase.Params, Single<Pair<String, Long>>> {

    override fun invoke(param: Params): Single<Pair<String, Long>> {
        return getAllNotifications(GetAllNotificationsUseCase.Params(param.uid))
            .map {
                it.map { it.appName to it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
                    .maxBy { it.second }
                    ?: "" to 0L
            }
    }

    class Params(val uid: String)

}

typealias GetLongestNotificationIdled = ParamizedUseCase<GetLongestNotificationIdledUseCase.Params, Single<Pair<String, Long>>>

val GetLongestNotificationIdledClass = ParamizedUseCase::class.java