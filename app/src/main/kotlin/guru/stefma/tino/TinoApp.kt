package guru.stefma.tino

import android.app.Application
import guru.stefma.tino.domain.di.*

class TinoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        dependencyGraph = DaggerDependencyGraph.builder()
            .generalModule(GeneralModule(this))
            .authenticationModule(AuthenticationModule())
            .dataModule(DataModule())
            .build()
    }
}

lateinit var dependencyGraph: DependencyGraph
    private set