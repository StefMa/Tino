package guru.stefma.tino.presentation.statistics.app.single

import de.halfbit.knot3.knot
import guru.stefma.tino.domain.model.ApplicationStatistics
import guru.stefma.tino.domain.usecase.GetStatisticsForApplicationId
import guru.stefma.tino.presentation.util.viewmodel.DisposableViewModel
import guru.stefma.tino.presentation.util.viewmodel.ViewModelHolder
import kotlinx.coroutines.rx3.rxSingle
import javax.inject.Inject

class SingleAppStatisticsViewModel(
    uid: String,
    appId: String,
    getStatisticsForApplicationId: GetStatisticsForApplicationId
) : DisposableViewModel() {

    private val knot = knot<State, Change, Unit> {
        state {
            initial = State.Init
        }
        changes {
            reduce {
                when (it) {
                    is Change.Loaded -> when (this) {
                        State.Init -> State.Ready(it.statistics).only
                        else -> unexpected(it)
                    }
                }
            }
        }
        events {
            source {
                rxSingle { getStatisticsForApplicationId(uid, appId) }
                    .map<Change> { Change.Loaded(it) }
                    .toObservable()
            }
        }
    }.also { disposables.add(it) }

    val statistics = knot.state
        .ofType(State.Ready::class.java)
        .map { it.statistics }

    private sealed class State {
        object Init : State()
        data class Ready(val statistics: ApplicationStatistics) : State()
    }

    private sealed class Change {
        data class Loaded(val statistics: ApplicationStatistics) : Change()
    }

}

class SingleAppStatisticsViewModelHolder @Inject constructor(
    private val getStatisticsForApplicationId: GetStatisticsForApplicationId
) : ViewModelHolder<Pair<@JvmSuppressWildcards String, @JvmSuppressWildcards String>, SingleAppStatisticsViewModel>() {
    override fun create(params: Pair<String, String>): SingleAppStatisticsViewModel {
        return SingleAppStatisticsViewModel(
            params.first,
            params.second,
            getStatisticsForApplicationId
        )
    }
}