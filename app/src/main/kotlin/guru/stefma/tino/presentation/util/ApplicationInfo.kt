package guru.stefma.tino.presentation.util

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import javax.inject.Inject

interface AppIdToAppNameConverter {
    operator fun invoke(appId: String): String
}

class DefaultAppIdToAppNameConverter @Inject constructor(
    private val appContext: Application
) : AppIdToAppNameConverter {
    override fun invoke(appId: String): String = appId.toAppName(appContext)
}

private fun String.toAppName(context: Context): String = context.labelForApplicationId(this)

private fun Context.labelForApplicationId(appId: String): String =
    try {
        packageManager.getApplicationInfo(appId, 0)
            .loadLabel(packageManager)
            .toString()
    } catch (exception: PackageManager.NameNotFoundException) {
        appId
    }