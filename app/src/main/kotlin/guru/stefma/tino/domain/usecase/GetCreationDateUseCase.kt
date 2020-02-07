package guru.stefma.tino.domain.usecase

import guru.stefma.tino.store.Store
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle
import javax.inject.Inject

class GetCreationDateUseCase @Inject constructor(
    private val store: Store
) : ParamizedUseCase<GetCreationDateUseCase.Params, Single<Long>> {

    override operator fun invoke(param: Params): Single<Long> =
        rxSingle { store.getCreationDate(param.uid) }

    class Params(val uid: String)
}

typealias GetCreationDate = ParamizedUseCase<GetCreationDateUseCase.Params, Single<Long>>

val GetCreationDateClass = ParamizedUseCase::class.java