package guru.stefma.tino.presentation.statistics.app

import androidx.lifecycle.ViewModel
import de.halfbit.knot.knot
import guru.stefma.tino.domain.usecase.GetAllApplicationIds
import guru.stefma.tino.domain.usecase.GetAllApplicationIdsUseCase
import guru.stefma.tino.presentation.util.viewmodel.ViewModelHolder
import io.reactivex.Observable
import javax.inject.Inject

class AppStatisticsViewModel(
    uid: String,
    getAllApplicationIds: GetAllApplicationIds
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
                    is Change.Filter -> when (this) {
                        is State.Ready -> {
                            val appIds = appIds.toMutableList().apply {
                                if (!it.checked) remove(it.appId) else add(it.appId)
                            }
                            copy(appIds).only
                        }
                        else -> unexpected(it)
                    }
                }
            }
        }
        events {
            source {
                getAllApplicationIds.invoke(GetAllApplicationIdsUseCase.Params(uid))
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

class AppStatisticsViewModelHolder @Inject constructor(
    private val getAllApplicationIds: GetAllApplicationIds
) : ViewModelHolder<String, AppStatisticsViewModel>() {
    override fun create(params: String): AppStatisticsViewModel {
        return AppStatisticsViewModel(params, getAllApplicationIds)
    }
}