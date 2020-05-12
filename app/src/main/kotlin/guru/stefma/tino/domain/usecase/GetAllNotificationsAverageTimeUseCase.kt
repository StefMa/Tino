package guru.stefma.tino.domain.usecase

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetAllNotificationsAverageTimeUseCase(
    private val getAllNotifications: GetAllNotifications,
    private val scheduler: Scheduler
) : ParamizedUseCase<GetAllNotificationsAverageTimeUseCase.Params, Observable<Long>> {

    @Inject
    constructor(getAllNotifications: GetAllNotifications) : this(
        getAllNotifications,
        Schedulers.computation()
    )

    override fun invoke(param: Params): Observable<Long> {
        return Observable.interval(0, 5, TimeUnit.SECONDS, scheduler)
            .flatMapSingle { getAllNotifications(GetAllNotificationsUseCase.Params(param.uid)) }
            .map { notifications ->
                if (notifications.isEmpty()) return@map 0L

                val notificationTime =
                    notifications
                        .map { it.notificationRemoveAt.unixtimestamp - it.notificationPostedAt.unixtimestamp }
                        .sum()

                notificationTime / notifications.size
            }
    }

    class Params(val uid: String)

}

typealias GetAllNotificationsAverageTime = ParamizedUseCase<GetAllNotificationsAverageTimeUseCase.Params, Observable<Long>>

val GetAllNotificationsAverageTimeClass = ParamizedUseCase::class.java