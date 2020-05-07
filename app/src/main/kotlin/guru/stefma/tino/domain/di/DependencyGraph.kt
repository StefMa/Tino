package guru.stefma.tino.domain.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.*
import dagger.multibindings.IntoMap
import guru.stefma.tino.R
import guru.stefma.tino.authentication.Authentication
import guru.stefma.tino.domain.usecase.*
import guru.stefma.tino.namegenerator.NameGenerator
import guru.stefma.tino.presentation.MainViewModel
import guru.stefma.tino.presentation.account.AccountViewModel
import guru.stefma.tino.presentation.statistics.StatisticsViewModelHolder
import guru.stefma.tino.presentation.statistics.app.AppStatisticsViewModelHolder
import guru.stefma.tino.presentation.statistics.app.single.SingleAppStatisticsViewModelHolder
import guru.stefma.tino.presentation.util.viewmodel.ViewModelFactory
import guru.stefma.tino.store.Store
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass

@Singleton
@Component(
    modules = [
        GeneralModule::class,
        AuthenticationModule::class,
        DataModule::class,
        UseCaseModule::class,
        ViewModelModule::class
    ]
)
interface DependencyGraph {
    val store: Store
    val authentication: Authentication

    val viewModelFactory: ViewModelProvider.Factory
}

@Module
class GeneralModule(
    private val application: Application
) {

    @Provides
    @Singleton
    fun provideStoreLocal(): Boolean = application.resources.getBoolean(R.bool.storeLocal)

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
    fun provideStore(storeLocal: Boolean): Store = Store(storeLocal)

}

@Module
abstract class UseCaseModule {

    @Binds
    abstract fun provideInitialSetup(impl: InitialSetupUseCase): InitialSetup

    @Binds
    abstract fun provideGenerateUserNameAndStore(impl: GenerateUserNameAndStoreUseCase): GenerateUserNameAndStore

    @Binds
    abstract fun provideGetAllNotifications(impl: GetAllNotificationsUseCase): GetAllNotifications

    @Binds
    abstract fun provideGetAllNotificationsAverageTime(impl: GetAllNotificationsAverageTimeUseCase): GetAllNotificationsAverageTime

    @Binds
    abstract fun provideGetAllNotificationsCount(impl: GetAllNotificationsCountUseCase): GetAllNotificationsCount

    @Binds
    abstract fun provideGetAllNotificationIdleTime(impl: GetAllNotificationsIdleTimeUseCase): GetAllNotificationsIdleTime

    @Binds
    abstract fun provideGetLongestNotificationIdled(impl: GetLongestNotificationIdledUseCase): GetLongestNotificationIdled

    @Binds
    abstract fun provideGetAppWithMostNotifications(impl: GetAppWithMostNotificationsUseCase): GetAppWithMostNotifications

    @Binds
    abstract fun provideGetCreationDate(impl: GetCreationDateUseCase): GetCreationDate

    @Binds
    abstract fun provideGetAllApplicationIds(impl: GetAllApplicationIdsUseCase): GetAllApplicationIds

    @Binds
    abstract fun provideGetAllNotificationsAverageTimeForApplicationId(impl: GetAllNotificationsAverageTimeForApplicationIdUseCase): GetAllNotificationsAverageTimeForApplicationId

    @Binds
    abstract fun provideGetAllNotificationsCountForApplicationId(impl: GetAllNotificationsCountForApplicationIdUseCase): GetAllNotificationsCountForApplicationId

    @Binds
    abstract fun provideGetAllNotificationsIdleTimeForApplicationId(impl: GetAllNotificationsIdleTimeForApplicationIdUseCase): GetAllNotificationsIdleTimeForApplicationId

    @Binds
    abstract fun provideGetLongestNotificationIdledForApplicationId(impl: GetLongestNotificationIdledForApplicationIdUseCase): GetLongestNotificationIdledForApplicationId

    @Binds
    abstract fun provideGetStatisticsForApplicationIdUseCase(impl: GetStatisticsForApplicationIdUseCase): GetStatisticsForApplicationId

}

@Module
abstract class ViewModelModule {

    @Binds
    @Singleton
    abstract fun viewModelFactory(impl: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun provideMainMainViewModel(impl: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(guru.stefma.tino.presentation.main.MainViewModel::class)
    abstract fun provideMainViewModel(impl: guru.stefma.tino.presentation.main.MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun provideAccountViewModel(impl: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StatisticsViewModelHolder::class)
    abstract fun provideStatisticsViewModelHolder(impl: StatisticsViewModelHolder): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AppStatisticsViewModelHolder::class)
    abstract fun provideAppStatisticsViewModelHolder(impl: AppStatisticsViewModelHolder): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SingleAppStatisticsViewModelHolder::class)
    abstract fun provideSingleAppStatisticsViewModelHolder(impl: SingleAppStatisticsViewModelHolder): ViewModel
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)