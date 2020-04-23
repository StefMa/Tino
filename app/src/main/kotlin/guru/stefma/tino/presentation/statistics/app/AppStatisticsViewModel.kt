package guru.stefma.tino.presentation.statistics.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import de.halfbit.knot.knot
import guru.stefma.tino.dependencyGraph
import guru.stefma.tino.domain.usecase.GetAllApplicationIds
import guru.stefma.tino.domain.usecase.GetAllApplicationIdsClass
import guru.stefma.tino.domain.usecase.GetAllApplicationIdsUseCase

fun ViewModelStoreOwner.createAppStatisticsViewModel(uid: String): AppStatisticsViewModel =
    ViewModelProvider(this, appStatisticsViewModelFactory(uid)).get(AppStatisticsViewModel::class.java)

private fun appStatisticsViewModelFactory(uid: String) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val allApplicationIds = dependencyGraph.getAllApplicationIds
        return modelClass
            .getDeclaredConstructor(
                String::class.java,
                GetAllApplicationIdsClass
            )
            .newInstance(
                uid,
                allApplicationIds
            )
    }
}

class AppStatisticsViewModel(
    uid: String,
    getStoredApplicationIds: GetAllApplicationIds
) : ViewModel() {

    private val knot = knot<State, Change, Unit> {
        state {
            initial = State.Init
        }
        changes {
            reduce {
                when (it) {
                    is Change.Loaded -> when (this) {
                        State.Init -> State.Ready(it.appIds).only
                        else -> unexpected(it)
                    }
                }
            }
        }
        events {
            source {
                getStoredApplicationIds.invoke(GetAllApplicationIdsUseCase.Params(uid))
                    .map<Change> { Change.Loaded(it) }
                    .toObservable()
            }
        }
    }

    val applicationIds = knot.state
        .ofType(State.Ready::class.java)
        .map { it.appIds.map { uid to it } }

    private sealed class State {
        object Init : State()
        data class Ready(val appIds: List<String>) : State()
    }

    private sealed class Change {
        data class Loaded(val appIds: List<String>) : Change()
    }

}