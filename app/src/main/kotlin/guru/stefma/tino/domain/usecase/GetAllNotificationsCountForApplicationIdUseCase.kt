package guru.stefma.tino.domain.usecase

import io.reactivex.Single
import javax.inject.Inject

class GetAllNotificationsCountForApplicationIdUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : ParamizedUseCase<GetAllNotificationsCountForApplicationIdUseCase.Params, Single<Int>> {

    override fun invoke(param: Params): Single<Int> {
        return getAllNotifications(GetAllNotificationsUseCase.Params(param.uid))
            .map { it.filter { it.appName == param.appId } }
            .map { it.count() }
    }

    class Params(val uid: String, val appId: String)
}

typealias GetAllNotificationsCountForApplicationId = ParamizedUseCase<GetAllNotificationsCountForApplicationIdUseCase.Params, Single<Int>>

val GetAllNotificationsCountForApplicationIdClass = ParamizedUseCase::class.java