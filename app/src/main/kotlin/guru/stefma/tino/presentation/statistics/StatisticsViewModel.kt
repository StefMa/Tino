package guru.stefma.tino.presentation.statistics

import androidx.lifecycle.ViewModel
import de.halfbit.knot3.knot
import guru.stefma.tino.domain.usecase.*
import guru.stefma.tino.presentation.util.viewmodel.ViewModelHolder
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Function5
import kotlinx.coroutines.rx3.rxSingle
import javax.inject.Inject

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
                Single.zip(
                    rxSingle { getAllNotificationsCount(uid) },
                    rxSingle { getAllNotificationsIdleTime(uid) },
                    rxSingle { getLongestNotificationIdled(uid) },
                    rxSingle { getAppWithMostNotifications(uid) },
                    rxSingle { getCreationDate(uid) },
                    Function5<Int, Long, Pair<String, Long>, Pair<String, Long>, Long, Change> { t1, t2, t3, t4, t5 ->
                        Change.Loaded(Data(t1, t2, t3, t4, t5))
                    }
                ).toObservable()
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

class StatisticsViewModelHolder @Inject constructor(
    private val getAllNotificationsCount: GetAllNotificationsCount,
    private val getAllNotificationsIdleTime: GetAllNotificationsIdleTime,
    private val getLongestNotificationIdled: GetLongestNotificationIdled,
    private val getAppWithMostNotifications: GetAppWithMostNotifications,
    private val getCreationDate: GetCreationDate
) : ViewModelHolder<String, StatisticsViewModel>() {
    override fun create(params: String): StatisticsViewModel {
        return StatisticsViewModel(
            params,
            getAllNotificationsCount,
            getAllNotificationsIdleTime,
            getLongestNotificationIdled,
            getAppWithMostNotifications,
            getCreationDate
        )
    }
}