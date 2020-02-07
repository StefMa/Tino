package guru.stefma.tino.domain.usecase

import io.reactivex.Single
import javax.inject.Inject

class GetAllNotificationsCountUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : ParamizedUseCase<GetAllNotificationsCountUseCase.Params, Single<Int>> {

    override fun invoke(param: Params): Single<Int> {
        return getAllNotifications(GetAllNotificationsUseCase.Params(param.uid))
            .map { it.count() }
    }

    class Params(val uid: String)
}

typealias GetAllNotificationsCount = ParamizedUseCase<GetAllNotificationsCountUseCase.Params, Single<Int>>

val GetAllNotificationsCountClass = ParamizedUseCase::class.java