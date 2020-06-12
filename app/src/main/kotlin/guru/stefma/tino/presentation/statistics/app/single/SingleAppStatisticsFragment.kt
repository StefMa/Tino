package guru.stefma.tino.presentation.statistics.app.single

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import guru.stefma.tino.R
import guru.stefma.tino.presentation.util.asFormattedTime
import guru.stefma.tino.presentation.util.viewbinding.bind
import guru.stefma.tino.presentation.util.viewmodel.getViewModel
import guru.stefma.tino.screenshot.sharer.shareScreenshotFromView
import kotlinx.android.synthetic.main.fragment_single_app_statistics.*

private const val EXTRA_UID = "uid"
private const val EXTRA_APP_ID = "appId"
private const val EXTRA_APP_NAME = "appName"
private typealias ApplicationId = String

fun createSingleAppStatisticsFragment(
    uid: String,
    appId: ApplicationId,
    appName: String
): SingleAppStatisticsFragment =
    SingleAppStatisticsFragment().apply {
        arguments = Bundle().apply {
            putString(EXTRA_UID, uid)
            putString(EXTRA_APP_ID, appId)
            putString(EXTRA_APP_NAME, appName)
        }
    }

class SingleAppStatisticsFragment : Fragment() {

    private val uid by lazy {
        arguments?.getString(EXTRA_UID) ?: throw IllegalAccessException(
            "You have to create an instance of the SingleAppStatisticsFragment via 'createSingleAppStatisticsFragment'"
        )
    }

    private val appId: ApplicationId by lazy {
        arguments?.getString(EXTRA_APP_ID) ?: throw IllegalAccessException(
            "You have to create an instance of the SingleAppStatisticsFragment via 'createSingleAppStatisticsFragment'"
        )
    }

    private val appName: ApplicationId by lazy {
        arguments?.getString(EXTRA_APP_NAME) ?: throw IllegalAccessException(
            "You have to create an instance of the SingleAppStatisticsFragment via 'createSingleAppStatisticsFragment'"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_single_app_statistics, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        share.setOnClickListener {
            shareScreenshotFromView(shareableView, requireActivity().window, appName)
        }

        val viewModel = getViewModel<SingleAppStatisticsViewModelHolder>().get(uid to appId)
        viewModel.bind()
    }

    private fun SingleAppStatisticsViewModel.bind() {
        bind(statistics) {
            notificationCount.text = getString(
                R.string.statistics_single_app_notification_count,
                it.notificationCount
            )
            notificationIdleTime.text = getString(
                R.string.statistics_single_app_notification_idle_time,
                it.allNotificationsIdleTime.asFormattedTime
            )
            longestNotificationIdleTime.text = getString(
                R.string.statistics_single_app_notification_longest_idle_time,
                it.longestNotificationIdleTime.asFormattedTime
            )
            averageNotificationIdleTime.text = getString(
                R.string.statistics_single_app_notification_average,
                it.allNotificationsAverageTime.asFormattedTime
            )
        }
    }
}