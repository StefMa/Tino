package guru.stefma.tino.domain.usecase

import guru.stefma.tino.domain.model.ApplicationStatistics
import javax.inject.Inject

interface GetStatisticsForApplicationId {
    suspend operator fun invoke(uid: String, appId: String): ApplicationStatistics
}

class GetStatisticsForApplicationIdUseCase @Inject constructor(
    private val getAllNotificationsCountForApplicationId: GetAllNotificationsCountForApplicationId,
    private val getAllNotificationsIdleTimeForApplicationId: GetAllNotificationsIdleTimeForApplicationId,
    private val getLongestNotificationIdledForApplicationId: GetLongestNotificationIdledForApplicationId,
    private val getAllNotificationsAverageTimeForApplicationId: GetAllNotificationsAverageTimeForApplicationId
) : GetStatisticsForApplicationId {

    override suspend fun invoke(uid: String, appId: String): ApplicationStatistics {


        val allNotificationsCountForApplicationId =
            getAllNotificationsCountForApplicationId(uid, appId)
        val allNotificationsIdleTimeForApplicationId =
            getAllNotificationsIdleTimeForApplicationId(uid, appId)
        val longestNotificationIdledForApplicationId =
            getLongestNotificationIdledForApplicationId(uid, appId)
        val allNotificationsAverageTimeForApplicationId =
            getAllNotificationsAverageTimeForApplicationId(uid, appId)
        return ApplicationStatistics(
            allNotificationsCountForApplicationId,
            allNotificationsIdleTimeForApplicationId,
            longestNotificationIdledForApplicationId,
            allNotificationsAverageTimeForApplicationId
        )
    }

}