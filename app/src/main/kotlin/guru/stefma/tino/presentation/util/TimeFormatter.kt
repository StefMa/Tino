package guru.stefma.tino.presentation.util

import java.util.concurrent.TimeUnit

val Long.asFormattedTime: String
    get() {
        if (this < 60)
            return "${this}s"
        if (this < TimeUnit.HOURS.toSeconds(1)) {
            val minutes = this / 60
            val seconds = this % 60
            return "${minutes.padStart()}m:${seconds.padStart()}s"
        }
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        val seconds = (this % 3600) % 60
        return "${hours.padStart()}h:${minutes.padStart()}m:${seconds.padStart()}s"
    }

private fun Long.padStart() = toString().padStart(2, '0')