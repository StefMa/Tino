package guru.stefma.tino.domain.usecase

import javax.inject.Inject

interface GetAllApplicationIds {
    suspend operator fun invoke(uid: String): List<String>
}

class GetAllApplicationIdsUseCase @Inject constructor(
    private val getAllNotifications: GetAllNotifications
) : GetAllApplicationIds {

    override suspend fun invoke(uid: String): List<String> {
        return getAllNotifications(uid)
            .map { it.appName }
            .distinct()
    }

}