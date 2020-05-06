package guru.stefma.tino.domain.usecase

import guru.stefma.tino.store.Store
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle
import javax.inject.Inject

class GetAllNotificationsUseCase @Inject constructor(
    private val store: Store
) : GetAllNotifications {

    override fun invoke(param: Params): Single<List<Store.Data>> {
        return rxSingle { store.getAll(param.uid) }
    }

    class Params(val uid: String)
}

typealias GetAllNotifications = ParamizedUseCase<GetAllNotificationsUseCase.Params, Single<List<@JvmSuppressWildcards Store.Data>>>