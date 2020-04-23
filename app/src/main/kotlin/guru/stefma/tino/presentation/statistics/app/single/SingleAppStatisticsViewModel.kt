package guru.stefma.tino.presentation.statistics.app.single

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import de.halfbit.knot.knot
import guru.stefma.tino.dependencyGraph
import guru.stefma.tino.domain.model.ApplicationStatistics
import guru.stefma.tino.domain.usecase.GetStatisticsForApplicationId
import guru.stefma.tino.domain.usecase.GetStatisticsForApplicationIdClass
import guru.stefma.tino.domain.usecase.GetStatisticsForApplicationIdUseCase

fun ViewModelStoreOwner.createSingleAppStatisticsViewModel(uid: String, appId: String): SingleAppStatisticsViewModel =
    ViewModelProvider(this, singleAppStatisticsViewModelFactory(uid, appId))
        .get(SingleAppStatisticsViewModel::class.java)

private fun singleAppStatisticsViewModelFactory(uid: String, appId: String) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val statisticsForApplicationId = dependencyGraph.getStatisticsForApplicationId
        return modelClass
            .getDeclaredConstructor(
                String::class.java,
                String::class.java,
                GetStatisticsForApplicationIdClass
            )
            .newInstance(
                uid,
                appId,
                statisticsForApplicationId
            )
    }
}

class SingleAppStatisticsViewModel(
    uid: String,
    appId: String,
    getStatisticsForApplicationId: GetStatisticsForApplicationId
) : ViewModel() {

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
                getStatisticsForApplicationId(GetStatisticsForApplicationIdUseCase.Params(uid, appId))
                    .map<Change> { Change.Loaded(it) }
                    .toObservable()
            }
        }
    }

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