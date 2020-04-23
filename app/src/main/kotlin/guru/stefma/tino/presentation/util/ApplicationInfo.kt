package guru.stefma.tino.presentation.util

import android.content.Context
import android.content.pm.PackageManager

fun Context.labelForApplicationId(appId: String): CharSequence =
    try {
        packageManager.getApplicationInfo(appId, PackageManager.MATCH_UNINSTALLED_PACKAGES)
            .loadLabel(packageManager)
    } catch (exception: PackageManager.NameNotFoundException) {
        appId
    }