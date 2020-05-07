package guru.stefma.tino.presentation.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import guru.stefma.tino.R
import guru.stefma.tino.presentation.statistics.app.createAppStatisticsFragment
import guru.stefma.tino.presentation.util.asFormattedTime
import guru.stefma.tino.presentation.util.navigation.showFragment
import guru.stefma.tino.presentation.util.viewbinding.bind
import guru.stefma.tino.presentation.util.viewmodel.getViewModel
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.util.*

private const val EXTRA_UID = "uid"

fun createStatisticsFragment(uid: String): StatisticsFragment =
    StatisticsFragment().apply {
        arguments = Bundle().apply {
            putString(EXTRA_UID, uid)
        }
    }

class StatisticsFragment : Fragment() {

    private val uid by lazy {
        arguments?.getString(EXTRA_UID) ?: throw IllegalAccessException(
            "You have to create an instance of the StatisticsFragment via 'createStatisticsFragment'"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statisticsPerApp.setOnClickListener {
            showFragment(createAppStatisticsFragment(uid))
        }

        val viewModel = getViewModel<StatisticsViewModelHolder>().get(uid)
        viewModel.bind()
    }

    private fun StatisticsViewModel.bind() {
        bind(data) {
            notificationCount.text = getString(
                R.string.statistics_notification_count,
                it.notificationCount
            )
            notificationIdleTime.text = getString(
                R.string.statistics_notification_idle_time,
                it.notificationIdleTime.asFormattedTime
            )
            mostNotificationFromApp.text = getString(
                R.string.statistics_notification_most_from_app,
                it.appWithMostNotifications.first,
                it.appWithMostNotifications.second
            )
            longestNotificationIdleTimeFromApp.text = getString(
                R.string.statistics_notification_longest_idle_time_from_app,
                it.longestIdled.second.asFormattedTime,
                it.longestIdled.first
            )
            collectStatisticSince.text = Date(it.creationDate * 1000).toString()
        }
    }

}