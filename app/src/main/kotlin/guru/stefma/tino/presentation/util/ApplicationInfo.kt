package guru.stefma.tino.presentation.util

import android.content.Context
import android.content.pm.PackageManager

fun Context.labelForApplicationId(appId: String): String =
    try {
        packageManager.getApplicationInfo(appId, PackageManager.MATCH_UNINSTALLED_PACKAGES)
            .loadLabel(packageManager)
            .toString()
    } catch (exception: PackageManager.NameNotFoundException) {
        appId
    }

fun String.toAppName(context: Context): String = context.labelForApplicationId(this)