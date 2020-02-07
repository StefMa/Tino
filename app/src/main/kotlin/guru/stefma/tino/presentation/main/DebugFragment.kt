package guru.stefma.tino.presentation.main

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import guru.stefma.tino.R
import guru.stefma.tino.dependencyGraph
import kotlinx.android.synthetic.main.fragment_debug.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DebugFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_debug, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        testNotification.setOnClickListener {
            createNotificationGroup()
            sendNotification()
        }

        enableNotificationListenerAccess.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        displayStoredNotifications.setOnClickListener {
            GlobalScope.launch {
                val uid = dependencyGraph.authentication.uid
                val storedNotifications = dependencyGraph.store.getAll(uid)
                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(requireContext())
                        .setMessage(storedNotifications.toString())
                        .show()
                }
            }
        }

        userUniqueId.text = "UID: ${dependencyGraph.authentication.uid}"
    }

    private fun createNotificationGroup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("0", "A group", importance)
            val notificationManager = NotificationManagerCompat.from(requireContext())
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        val noti: Notification = NotificationCompat.Builder(requireContext(), "0")
            .setSmallIcon(R.drawable.ic_dino_filled)
            .setContentTitle("Just a test")
            .setColorized(true)
            .setColor(requireContext().getColor(R.color.colorPrimary))
            .setContentText("Some text")
            .build()
        NotificationManagerCompat.from(requireContext()).notify(0, noti)
    }
}