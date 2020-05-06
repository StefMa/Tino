package guru.stefma.tino.presentation.statistics.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import de.halfbit.knot.knot
import guru.stefma.tino.dependencyGraph
import guru.stefma.tino.domain.usecase.GetAllApplicationIds
import guru.stefma.tino.domain.usecase.GetAllApplicationIdsClass
import guru.stefma.tino.domain.usecase.GetAllApplicationIdsUseCase
import io.reactivex.Observable
import kotlinx.coroutines.rx2.awaitFirst

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
                        State.Init -> State.Ready(it.appIds.sorted()).only
                        else -> unexpected(it)
                    }
                    is Change.Filter -> when (this) {
                        is State.Ready -> {
                            val appIds = appIds.toMutableList().apply {
                                if (!it.checked) remove(it.appId) else add(it.appId)
                            }
                            copy(appIds.sorted()).only
                        }
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

    val appStatisticsInfo: Observable<List<AppStatisticsInformation>> = knot.state
        .ofType(State.Ready::class.java)
        .map { it.appIds.map { appId -> AppStatisticsInformation(uid, appId) } }

    val filterItems = knot.state
        .ofType(State.Ready::class.java)
        .take(1)
        .map {
            val appIds = it.appIds
            appIds.map { appId ->
                val onFilterItemClick: (Boolean) -> Unit = { checked ->
                    knot.change.accept(Change.Filter(appIds, appId, checked))
                }
                FilterItem(appId, true, onFilterItemClick)
            }
        }

    private sealed class State {
        object Init : State()
        data class Ready(val appIds: List<String>) : State()
    }

    private sealed class Change {
        data class Loaded(val appIds: List<String>) : Change()
        data class Filter(val appIds: List<String>, val appId: String, val checked: Boolean) : Change()
    }

}

data class AppStatisticsInformation(
    val uid: String,
    val appId: String
)

data class FilterItem(
    val appId: String,
    val checked: Boolean,
    val onCheckedChanged: (Boolean) -> Unit
)