package guru.stefma.tino.screenshot.sharer

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun shareScreenshotFromView(view: View, window: Window, shareText: String? = null) {
    ScreenshotCreator().create(view, window) {
        saveBitmap(window.context, it) { imageFile ->
            imageFile?.let { image ->
                shareImage(window.context, image, shareText)
            }
        }
    }
}

private fun saveBitmap(context: Context, bitmap: Bitmap, block: (File?) -> Unit) =
    try {
        val cachePath = File(context.cacheDir, "screenshotToShare").also { it.mkdirs() }
        val imageFile = File(cachePath, "image.png")
        FileOutputStream(imageFile).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        block(imageFile)
    } catch (e: IOException) {
        e.printStackTrace()
        block(null)
    }

private fun shareImage(context: Context, imageFile: File, shareText: String?) {
    val contentUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.screenshot.sharer.fileprovider",
        imageFile
    )

    if (contentUri != null) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.setDataAndType(contentUri, context.contentResolver.getType(contentUri))
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        shareText?.let { shareIntent.putExtra(Intent.EXTRA_TEXT, it) }
        ContextCompat.startActivity(
            context,
            Intent.createChooser(
                shareIntent,
                "Choose an app"
            ),
            null
        )
    }
}
