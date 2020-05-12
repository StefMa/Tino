package guru.stefma.tino.domain.usecase

import guru.stefma.tino.store.Store
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.rx3.rxSingle
import javax.inject.Inject

class GetCreationDateUseCase @Inject constructor(
    private val store: Store
) : GetCreationDate {

    override operator fun invoke(param: Params): Single<Long> =
        rxSingle { store.getCreationDate(param.uid) }

    class Params(val uid: String)
}

typealias GetCreationDate = ParamizedUseCase<GetCreationDateUseCase.Params, Single<Long>>