package guru.stefma.tino.presentation.account

import androidx.lifecycle.ViewModel
import de.halfbit.knot3.knot
import guru.stefma.tino.authentication.Authentication
import guru.stefma.tino.presentation.util.Signaler
import guru.stefma.tino.store.Store
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.rx3.rxSingle
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val authentication: Authentication,
    private val store: Store
) : ViewModel() {

    private val showUserStatisticsSignal = Signaler<String>()
    private val showUserNameSaveFailedSignal = Signaler<Unit>()
    private val showUserNameDoesNotExistSignal = Signaler<String>()

    private val knot = knot<State, Change, Action> {
        state {
            initial = State.Init
        }
        changes {
            reduce {
                when (it) {
                    is Change.User.Loaded -> when (this) {
                        State.Init -> State.Ready(it.username).only
                        else -> unexpected(it)
                    }
                    is Change.User.SaveUserName -> when (this) {
                        is State.Ready -> State.Ready(it.username) + Action.SaveUserName(it.username)
                        else -> unexpected(it)
                    }
                    is Change.User.SaveSuccess -> when (this) {
                        is State.Ready -> only
                        else -> unexpected(it)
                    }
                    is Change.User.SaveFailed -> when (this) {
                        is State.Ready -> {
                            showUserNameSaveFailedSignal.send(Unit)
                            only
                        }
                        else -> unexpected(it)
                    }
                    is Change.Statistics.SeeStatisticsFrom -> when (this) {
                        is State.Init -> only + Action.SearchUidForUsername(it.username)
                        is State.Ready -> only + Action.SearchUidForUsername(it.username)
                    }
                    is Change.Statistics.ShowStatisticsFrom -> when (this) {
                        is State.Init -> {
                            showUserStatisticsSignal.send(it.uid)
                            only
                        }
                        is State.Ready -> {
                            showUserStatisticsSignal.send(it.uid)
                            only
                        }
                    }
                    is Change.Statistics.ShowStatisticsFromError -> when (this) {
                        is State.Init -> {
                            showUserNameDoesNotExistSignal.send(it.username)
                            only
                        }
                        is State.Ready -> {
                            showUserNameDoesNotExistSignal.send(it.username)
                            only
                        }
                    }
                }
            }
        }
        events {
            source {
                rxSingle { store.getUserName(authentication.uid) ?: "" }
                    .map { Change.User.Loaded(it) as Change }
                    .toObservable()
            }
        }
        actions {
            perform<Action.SaveUserName> {
                this
                    .flatMapSingle {
                        rxSingle { store.setUserName(authentication.uid, it.username) }
                            .map { Change.User.SaveSuccess as Change }
                            .onErrorReturn { Change.User.SaveFailed }
                    }
            }

            perform<Action.SearchUidForUsername> {
                this
                    .flatMapSingle { action ->
                        rxSingle { store.getUid(action.username) }
                            .onErrorReturn { "" }
                            .map { it to action.username }
                    }
                    .map { (uid, username) ->
                        if (uid.isNotEmpty()) Change.Statistics.ShowStatisticsFrom(uid)
                        else Change.Statistics.ShowStatisticsFromError(username)
                    }
            }
        }
    }

    val username = knot.state
        .ofType(State.Ready::class.java)
        .map { it.username }

    val showUserStatistics: Observable<String> = showUserStatisticsSignal.origin

    val showUserNameSaveFailed: Observable<Unit> = showUserNameSaveFailedSignal.origin

    val showUserNameDoesNotExistError: Observable<String> = showUserNameDoesNotExistSignal.origin

    fun saveUseName(username: String) =
        knot.change.accept(Change.User.SaveUserName(username))

    fun seeStatisticsFrom(username: String) =
        knot.change.accept(Change.Statistics.SeeStatisticsFrom(username.trim()))

    private sealed class State {
        object Init : State()
        data class Ready(val username: String) : State()
    }

    private sealed class Change {
        sealed class User : Change() {
            data class Loaded(val username: String) : User()
            data class SaveUserName(val username: String) : User()
            object SaveSuccess : User()
            object SaveFailed : User()
        }

        sealed class Statistics : Change() {
            data class SeeStatisticsFrom(val username: String) : Statistics()
            data class ShowStatisticsFrom(val uid: String) : Statistics()
            data class ShowStatisticsFromError(val username: String) : Statistics()
        }
    }

    private sealed class Action {
        data class SaveUserName(val username: String) : Action()
        data class SearchUidForUsername(val username: String) : Action()
    }
}