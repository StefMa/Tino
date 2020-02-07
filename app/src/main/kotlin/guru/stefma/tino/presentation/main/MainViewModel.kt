package guru.stefma.tino.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import de.halfbit.knot.knot
import guru.stefma.tino.authentication.Authentication
import guru.stefma.tino.dependencyGraph
import guru.stefma.tino.domain.usecase.GetAllNotificationsAverageTime
import guru.stefma.tino.domain.usecase.GetAllNotificationsAverageTimeClass
import guru.stefma.tino.domain.usecase.GetAllNotificationsAverageTimeUseCase

fun ViewModelStoreOwner.createMainViewModel(): MainViewModel =
    ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)

private val mainViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val authentication = dependencyGraph.authentication
        val allNotificationsAverageTime = dependencyGraph.getAllNotificationsAverageTime
        return modelClass
            .getDeclaredConstructor(
                Authentication::class.java,
                GetAllNotificationsAverageTimeClass
            )
            .newInstance(
                authentication,
                allNotificationsAverageTime
            )
    }
}

class MainViewModel(
    authentication: Authentication,
    getAllNotificationsAverageTime: GetAllNotificationsAverageTime
) : ViewModel() {

    private val knot = knot<State, Change, Unit> {
        state {
            initial = State.Init
        }
        changes {
            reduce {
                when (it) {
                    is Change.AverageTimeLoaded -> when (this) {
                        State.Init -> State.Ready(it.time).only
                        is State.Ready -> this.copy(it.time).only
                    }
                }
            }
        }
        events {
            source {
                getAllNotificationsAverageTime(
                    GetAllNotificationsAverageTimeUseCase.Params(authentication.uid)
                )
                    .map { Change.AverageTimeLoaded(it) as Change }
            }
        }
    }

    val averageTime = knot.state
        .ofType(State.Ready::class.java)
        .map { it.averageTime }

    private sealed class State {
        object Init : State()
        data class Ready(val averageTime: Long) : State()
    }

    private sealed class Change {
        data class AverageTimeLoaded(val time: Long) : Change()
    }
}