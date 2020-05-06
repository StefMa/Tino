package guru.stefma.tino.domain.model

data class ApplicationStatistics(
    val notificationCount: Int,
    val allNotificationsIdleTime: Long,
    val longestNotificationIdleTime: Long,
    val allNotificationsAverageTime: Long
)