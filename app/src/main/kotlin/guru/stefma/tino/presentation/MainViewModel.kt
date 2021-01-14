package guru.stefma.tino.presentation

import de.halfbit.knot3.knot
import guru.stefma.tino.domain.usecase.InitialSetup
import guru.stefma.tino.presentation.util.viewmodel.DisposableViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import kotlinx.coroutines.rx3.rxObservable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val initialSetup: InitialSetup
) : DisposableViewModel() {

    private val knot = knot<State, Change, Action> {
        state {
            initial = State.Empty
        }

        changes {
            reduce { change ->
                when (change) {
                    is Change.ShowSplashscreen -> when (this) {
                        State.Empty -> State.Splashscreen + Action.InitSetup
                        else -> unexpected(change)
                    }
                    is Change.ShowMain -> when (this) {
                        State.Splashscreen -> State.Main.only
                        else -> unexpected(change)
                    }
                }
            }
        }

        actions {
            perform<Action.InitSetup> {
                this
                    .flatMap {
                        Observable.combineLatest(
                            rxObservable { send(initialSetup()) },
                            Observable.just("").delay(1500, TimeUnit.MILLISECONDS),
                            BiFunction<Boolean, String, Boolean> { initSetupSuccess, _ -> initSetupSuccess }
                        )
                    }
                    .filter { it } // TODO: Ugly workaround.. If setup was not successfully we have to provide a error message and retry!
                    .map { Change.ShowMain }
            }
        }

        events {
            source {
                Observable.just(Change.ShowSplashscreen)
            }
        }
    }.also { disposables.add(it) }

    val showSplashscreen = knot.state
        .map { it == State.Splashscreen }

    val showMain = knot.state
        .map { it == State.Main }

    private sealed class State {
        object Empty : State()
        object Splashscreen : State()
        object Main : State()
    }

    private sealed class Change {
        object ShowSplashscreen : Change()
        object ShowMain : Change()
    }

    private sealed class Action {
        object InitSetup : Action()
    }
}