package guru.stefma.tino.domain.usecase

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAllNotificationsCountUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : GetAllNotificationsCount {

    override fun invoke(param: Params): Single<Int> {
        return getAllNotifications(GetAllNotificationsUseCase.Params(param.uid))
            .map { it.count() }
    }

    class Params(val uid: String)
}

typealias GetAllNotificationsCount = ParamizedUseCase<GetAllNotificationsCountUseCase.Params, Single<Int>>