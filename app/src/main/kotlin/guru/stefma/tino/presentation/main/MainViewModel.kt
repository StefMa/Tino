package guru.stefma.tino.presentation.main

import androidx.lifecycle.ViewModel
import de.halfbit.knot.knot
import guru.stefma.tino.authentication.Authentication
import guru.stefma.tino.domain.usecase.GetAllNotificationsAverageTime
import guru.stefma.tino.domain.usecase.GetAllNotificationsAverageTimeUseCase
import javax.inject.Inject

class MainViewModel @Inject constructor(
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
            coldSource {
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
