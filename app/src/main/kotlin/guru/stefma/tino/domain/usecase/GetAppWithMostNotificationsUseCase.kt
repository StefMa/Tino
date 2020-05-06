package guru.stefma.tino.domain.usecase

import io.reactivex.Single
import javax.inject.Inject

class GetAppWithMostNotificationsUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : GetAppWithMostNotifications {

    override operator fun invoke(param: Params): Single<Pair<String, Long>> {
        return getAllNotifications(GetAllNotificationsUseCase.Params(param.uid))
            .map { notifications ->
                notifications.map { it.appName to it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
            }
            .map {
                it.groupBy { it.first }
                    .map { it.key to it.value.size.toLong() }
                    .maxBy { it.second }
                    ?: "" to 0L
            }
    }

    class Params(val uid: String)
}

typealias GetAppWithMostNotifications = ParamizedUseCase<GetAppWithMostNotificationsUseCase.Params, Single<Pair<@JvmSuppressWildcards String, @JvmSuppressWildcards Long>>>