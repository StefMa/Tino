package guru.stefma.tino.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import de.halfbit.knot.knot
import guru.stefma.tino.dependencyGraph
import guru.stefma.tino.domain.usecase.*
import io.reactivex.Observable
import io.reactivex.functions.Function5

fun ViewModelStoreOwner.createStatisticsViewModel(uid: String): StatisticsViewModel =
    ViewModelProvider(this, statisticsViewModelFactory(uid)).get(StatisticsViewModel::class.java)

private fun statisticsViewModelFactory(uid: String) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val allNotificationsCount = dependencyGraph.getAllNotificationsCount
        val allNotificationIdleTime = dependencyGraph.getAllNotificationIdleTime
        val longestNotificationIdled = dependencyGraph.getLongestNotificationIdled
        val appWithMostNotifications = dependencyGraph.getAppWithMostNotifications
        val getCreationDate = dependencyGraph.getCreationDate
        return modelClass
            .getDeclaredConstructor(
                String::class.java,
                GetAllNotificationsCountClass,
                GetAllNotificationsIdleTimeClass,
                GetLongestNotificationIdledClass,
                GetAppWithMostNotificationsClass,
                GetCreationDateClass
            )
            .newInstance(
                uid,
                allNotificationsCount,
                allNotificationIdleTime,
                longestNotificationIdled,
                appWithMostNotifications,
                getCreationDate
            )
    }
}

class StatisticsViewModel(
    uid: String,
    getAllNotificationsCount: GetAllNotificationsCount,
    getAllNotificationsIdleTime: GetAllNotificationsIdleTime,
    getLongestNotificationIdled: GetLongestNotificationIdled,
    getAppWithMostNotifications: GetAppWithMostNotifications,
    getCreationDate: GetCreationDate
) : ViewModel() {

    private val knot = knot<State, Change, Unit> {
        state {
            initial = State.Init
        }
        changes {
            reduce {
                when (it) {
                    is Change.Loaded -> when (this) {
                        State.Init -> State.Ready(it.data).only
                        else -> unexpected(it)
                    }
                }
            }
        }
        events {
            source {
                Observable.combineLatest(
                    getAllNotificationsCount(
                        GetAllNotificationsCountUseCase.Params(uid)
                    ).toObservable(),
                    getAllNotificationsIdleTime(
                        GetAllNotificationIdleTimeUseCase.Params(uid)
                    ).toObservable(),
                    getLongestNotificationIdled(
                        GetLongestNotificationIdledUseCase.Params(uid)
                    ).toObservable(),
                    getAppWithMostNotifications(
                        GetAppWithMostNotificationsUseCase.Params(uid)
                    ).toObservable(),
                    getCreationDate(
                        GetCreationDateUseCase.Params(uid)
                    ).toObservable(),
                    Function5<Int, Long, Pair<String, Long>, Pair<String, Long>, Long, Change.Loaded> { t1, t2, t3, t4, t5 ->
                        Change.Loaded(Data(t1, t2, t3, t4, t5))
                    }
                )
            }
        }
    }

    val data = knot.state
        .ofType(State.Ready::class.java)
        .map { it.data }

    data class Data(
        val notificationCount: Int,
        val notificationIdleTime: Long,
        val longestIdled: Pair<String, Long>,
        val appWithMostNotifications: Pair<String, Long>,
        val creationDate: Long
    )

    private sealed class State {
        object Init : State()
        data class Ready(val data: Data) : State()
    }

    private sealed class Change {
        data class Loaded(val data: Data) : Change()
    }

}