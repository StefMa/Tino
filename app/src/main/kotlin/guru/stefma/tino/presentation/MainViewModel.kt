package guru.stefma.tino.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import de.halfbit.knot.knot
import guru.stefma.tino.dependencyGraph
import guru.stefma.tino.domain.usecase.InitialSetup
import guru.stefma.tino.domain.usecase.InitialSetupClass
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

fun ViewModelStoreOwner.createMainViewModel(): MainViewModel =
    ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)

private val mainViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val initialSetup = dependencyGraph.initialSetup
        return modelClass
            .getDeclaredConstructor(InitialSetupClass)
            .newInstance(initialSetup)
    }
}

class MainViewModel(
    private val initialSetup: InitialSetup
) : ViewModel() {

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
                            initialSetup().toObservable(),
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
    }

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