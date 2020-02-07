package guru.stefma.tino.presentation.util

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class TimeFormatterTest {

    @Test
    fun `60 seconds below should not format`() {
        val from = TimeUnit.SECONDS.toSeconds(0)
        val to = TimeUnit.SECONDS.toSeconds(60)
        for (i in from until to) {
            val formattedTime = i.asFormattedTime
            //println(formattedTime)
            assertThat(formattedTime)
                .isEqualTo("${i}s")
        }
    }

    @Test
    fun `60 seconds or above AND below 60 minutes should format to minutes and seconds`() {
        val from = TimeUnit.SECONDS.toSeconds(60)
        val to = TimeUnit.MINUTES.toSeconds(60)
        for (i in from until to) {
            val formattedTime = i.asFormattedTime
            //println(formattedTime)
            val minutes = i / 60
            val minutesString = minutes.toString().padStart(2, '0')
            val seconds = i % 60
            val secondsString = seconds.toString().padStart(2, '0')
            assertThat(formattedTime)
                .isEqualTo("${minutesString}m:${secondsString}s")
        }
    }

    @Test
    fun `60 minutes or above should format in hours, minutes and seconds`() {
        val from = TimeUnit.MINUTES.toSeconds(60)
        val to = TimeUnit.HOURS.toSeconds(2)
        for (i in from until to) {
            val formattedTime = i.asFormattedTime
            //println(formattedTime)
            val hours = i / 3600
            val hoursString = hours.toString().padStart(2, '0')
            val minutes = (i % 3600) / 60
            val minutesString = minutes.toString().padStart(2, '0')
            val seconds = (i % 3600) % 60
            val secondsString = seconds.toString().padStart(2, '0')
            assertThat(formattedTime)
                .isEqualTo("${hoursString}h:${minutesString}m:${secondsString}s")
        }
    }

}