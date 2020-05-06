package guru.stefma.tino.domain.di

import android.app.Application
import dagger.Component
import dagger.Module
import dagger.Provides
import guru.stefma.tino.R
import guru.stefma.tino.authentication.Authentication
import guru.stefma.tino.domain.usecase.*
import guru.stefma.tino.namegenerator.NameGenerator
import guru.stefma.tino.store.Store
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
@Component(
    modules = [
        GeneralModule::class,
        AuthenticationModule::class,
        DataModule::class,
        UseCaseModule::class
    ]
)
interface DependencyGraph {
    val store: Store
    val authentication: Authentication

    val initialSetup: InitialSetupUseCase
    val getAllNotifications: GetAllNotificationsUseCase
    val getAllNotificationsCount: GetAllNotificationsCountUseCase
    val getAllNotificationIdleTime: GetAllNotificationIdleTimeUseCase
    val getAppWithMostNotifications: GetAppWithMostNotificationsUseCase
    val getLongestNotificationIdled: GetLongestNotificationIdledUseCase
    val getAllNotificationsAverageTime: GetAllNotificationsAverageTimeUseCase
    val getCreationDate: GetCreationDateUseCase
    val getAllApplicationIds: GetAllApplicationIdsUseCase
    val getStatisticsForApplicationId: GetStatisticsForApplicationIdUseCase
}

@Module
class GeneralModule(
    private val application: Application
) {

    @Provides
    @Singleton
    fun provideStoreLocal() = application.resources.getBoolean(R.bool.storeLocal)

    @Provides
    @Singleton
    fun provideNameGenerator() = NameGenerator()
}

@Module
class AuthenticationModule {

    @Provides
    @Singleton
    fun provideAuthentication(storeLocal: Boolean): Authentication =
        if (!storeLocal) Authentication() else StubAuthentication()

    private class StubAuthentication : Authentication {
        override val uid: String = "1"

        override suspend fun createAnonymousUser() = suspendCoroutine<Unit> {
            it.resume(Unit)
        }
    }
}

@Module
class DataModule {

    @Provides
    fun provideStore(storeLocal: Boolean): Store =
        Store(storeLocal)

}

@Module
class UseCaseModule {

    @Provides
    fun provideGetAllNotifications(store: Store): GetAllNotifications =
        GetAllNotificationsUseCase(store)

    @Provides
    fun provideGenerateUserNameAndStore(
        store: Store,
        nameGenerator: NameGenerator,
        authentication: Authentication
    ): GenerateUserNameAndStore =
        GenerateUserNameAndStoreUseCase(store, nameGenerator, authentication)

    @Provides
    fun provideGetAllNotificationsCountForApplicationId(getAllNotifications: GetAllNotifications): GetAllNotificationsCountForApplicationId =
        GetAllNotificationsCountForApplicationIdUseCase(getAllNotifications)

    @Provides
    fun provideGetAllNotificationsIdleTimeForApplicationId(getAllNotifications: GetAllNotifications): GetAllNotificationsIdleTimeForApplicationId =
        GetAllNotificationsIdleTimeForApplicationIdUseCase(getAllNotifications)

    @Provides
    fun provideGetLongestNotificationIdledForApplicationId(getAllNotifications: GetAllNotifications): GetLongestNotificationIdledForApplicationId =
        GetLongestNotificationIdledForApplicationIdUseCase(getAllNotifications)

    @Provides
    fun provideGetAllNotificationsAverageTimeForApplicationIdUseCase(getAllNotifications: GetAllNotifications): GetAllNotificationsAverageTimeForApplicationId =
        GetAllNotificationsAverageTimeForApplicationIdUseCase(getAllNotifications)
}