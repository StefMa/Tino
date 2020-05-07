package guru.stefma.tino.presentation.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import guru.stefma.tino.R
import guru.stefma.tino.dependencyGraph
import guru.stefma.tino.presentation.account.AccountFragment
import guru.stefma.tino.presentation.statistics.createStatisticsFragment
import guru.stefma.tino.presentation.util.asFormattedTime
import guru.stefma.tino.presentation.util.navigation.showFragment
import guru.stefma.tino.presentation.util.viewbinding.bind
import guru.stefma.tino.presentation.util.viewmodel.getViewModel
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = getViewModel<MainViewModel>()
        viewModel.bind()

        sumNotificationIdleTime.setOnLongClickListener {
            showFragment(DebugFragment())
            true
        }

        statistics.setOnClickListener {
            val uid = dependencyGraph.authentication.uid
            showFragment(createStatisticsFragment(uid))
        }

        with(notificationAccess) {
            setOnClickListener { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
            setOnLongClickListener { showFragment(DebugFragment());true }
        }

        account.setOnClickListener { showFragment(AccountFragment()) }

        toggleNotificationAccess()
    }

    override fun onResume() {
        super.onResume()
        toggleNotificationAccess()
    }

    private fun toggleNotificationAccess() {
        val listenerPackages =
            NotificationManagerCompat.getEnabledListenerPackages(requireContext())
        if (!listenerPackages.contains(requireContext().packageName)) {
            notificationAccess.visibility = View.VISIBLE
            sumNotificationIdleTime.visibility = View.GONE
        } else {
            notificationAccess.visibility = View.GONE
            sumNotificationIdleTime.visibility = View.VISIBLE
        }
    }

    private fun MainViewModel.bind() {
        bind(averageTime) { sumNotificationIdleTime.text = it.asFormattedTime }
    }
}