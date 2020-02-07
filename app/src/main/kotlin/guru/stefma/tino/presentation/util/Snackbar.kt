package guru.stefma.tino.presentation.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(
    text: String,
    duration: Int = Snackbar.LENGTH_LONG
) = Snackbar.make(this, text, duration).show()