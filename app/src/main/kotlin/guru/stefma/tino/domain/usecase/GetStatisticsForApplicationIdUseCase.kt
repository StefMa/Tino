package guru.stefma.tino.domain.usecase

import guru.stefma.tino.domain.model.ApplicationStatistics
import io.reactivex.Single
import io.reactivex.functions.Function4
import javax.inject.Inject

class GetStatisticsForApplicationIdUseCase @Inject constructor(
    private val getAllNotificationsCountForApplicationId: GetAllNotificationsCountForApplicationId,
    private val getAllNotificationsIdleTimeForApplicationId: GetAllNotificationsIdleTimeForApplicationId,
    private val getLongestNotificationIdledForApplicationId: GetLongestNotificationIdledForApplicationId,
    private val getAllNotificationsAverageTimeForApplicationId: GetAllNotificationsAverageTimeForApplicationId
) : ParamizedUseCase<GetStatisticsForApplicationIdUseCase.Params, Single<ApplicationStatistics>> {

    override fun invoke(param: Params): Single<ApplicationStatistics> {
        return Single.zip(
            getAllNotificationsCountForApplicationId(
                GetAllNotificationsCountForApplicationIdUseCase.Params(
                    param.uid,
                    param.appId
                )
            ),
            getAllNotificationsIdleTimeForApplicationId(
                GetAllNotificationsIdleTimeForApplicationIdUseCase.Params(
                    param.uid,
                    param.appId
                )
            ),
            getLongestNotificationIdledForApplicationId(
                GetLongestNotificationIdledForApplicationIdUseCase.Params(
                    param.uid,
                    param.appId
                )
            ),
            getAllNotificationsAverageTimeForApplicationId(
                GetAllNotificationsAverageTimeForApplicationIdUseCase.Params(
                    param.uid,
                    param.appId
                )
            ),
            Function4<Int, Long, Long, Long, ApplicationStatistics> { count, allIdleTime, longestIdleTime, averageTime ->
                ApplicationStatistics(
                    notificationCount = count,
                    allNotificationsIdleTime = allIdleTime,
                    longestNotificationIdleTime = longestIdleTime,
                    allNotificationsAverageTime = averageTime
                )
            }
        )
    }

    class Params(val uid: String, val appId: String)

}

typealias GetStatisticsForApplicationId = ParamizedUseCase<GetStatisticsForApplicationIdUseCase.Params, Single<ApplicationStatistics>>

val GetStatisticsForApplicationIdClass = ParamizedUseCase::class.java