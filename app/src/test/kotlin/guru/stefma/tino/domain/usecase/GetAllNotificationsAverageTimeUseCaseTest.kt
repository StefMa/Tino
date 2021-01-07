package guru.stefma.tino.domain.usecase

import com.google.common.truth.Truth.assertThat
import guru.stefma.tino.store.NotificationPostedTime
import guru.stefma.tino.store.NotificationRemovedTime
import guru.stefma.tino.store.Store
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

class GetAllNotificationsAverageTimeUseCaseTest {

    private val mockGetAllNotifications = object : GetAllNotifications {
        override suspend fun invoke(uid: String): List<Store.Data> {
            return listOf(
                Store.Data(
                    "uid",
                    "appName",
                    NotificationPostedTime(0L),
                    NotificationRemovedTime(0L)
                )
            )
        }

    }

    private val useCase = GetAllNotificationsAverageTimeUseCase(mockGetAllNotifications)

    @Test
    fun `test use case should emit three times`() {
        runBlockingTest {
            val results = mutableListOf<Long>()
            val launch = launch {
                useCase.invoke("uid").collect { results.add(it) }
            }
            advanceTimeBy(12000)
            launch.cancel()
            assertThat(results.toList().size).isEqualTo(3)
        }
    }

}
