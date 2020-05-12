package guru.stefma.tino.domain.usecase

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAllApplicationIdsUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : GetAllApplicationIds {

    override fun invoke(param: Params): Single<List<String>> {
        return getAllNotifications(GetAllNotificationsUseCase.Params(param.uid))
            .map { it.map { it.appName }.distinct() }
    }

    class Params(val uid: String)
}

typealias GetAllApplicationIds = ParamizedUseCase<GetAllApplicationIdsUseCase.Params, Single<List<@JvmSuppressWildcards String>>>