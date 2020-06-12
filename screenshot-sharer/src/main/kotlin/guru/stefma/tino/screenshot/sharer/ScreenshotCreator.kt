package guru.stefma.tino.screenshot.sharer

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.view.PixelCopy
import android.view.View
import android.view.Window

internal interface ScreenshotCreator {
    companion object {
        operator fun invoke(): ScreenshotCreator =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ScreenshotSharerApi24Above() else ScreenshotSharerApi23()
    }

    fun create(view: View, window: Window, callback: (Bitmap) -> Unit)
}

internal class ScreenshotSharerApi24Above : ScreenshotCreator {
    override fun create(view: View, window: Window, callback: (Bitmap) -> Unit) {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val locationOfViewInWindow = IntArray(2)
        view.getLocationInWindow(locationOfViewInWindow)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PixelCopy.request(
                    window,
                    Rect(
                        locationOfViewInWindow[0],
                        locationOfViewInWindow[1],
                        locationOfViewInWindow[0] + view.width,
                        locationOfViewInWindow[1] + view.height
                    ), bitmap, { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) callback(bitmap)
                        // TODO: Handle other results..
                    },
                    Handler()
                )
            }
        } catch (e: IllegalArgumentException) {
            // PixelCopy may throw IllegalArgumentException, make sure to handle it
            e.printStackTrace()
        }
    }
}

internal class ScreenshotSharerApi23 : ScreenshotCreator {
    override fun create(view: View, window: Window, callback: (Bitmap) -> Unit) {
        val screenView = view.rootView
        screenView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(screenView.drawingCache)
        screenView.isDrawingCacheEnabled = false
        callback(bitmap)
    }
}