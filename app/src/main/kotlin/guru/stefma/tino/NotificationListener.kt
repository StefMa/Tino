package guru.stefma.tino

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import guru.stefma.tino.store.NotificationPostedTime
import guru.stefma.tino.store.NotificationRemovedTime
import guru.stefma.tino.store.Store
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (!sbn.isOngoing) { // Don't care about ongoing.. cause this is music for example
            val appName = sbn.packageName
            val notificationPosted = NotificationPostedTime(sbn.postTime / 1000)
            val notificationRemoved = NotificationRemovedTime(System.currentTimeMillis() / 1000)

            storeData(appName, notificationPosted, notificationRemoved)
        }
    }

    private fun storeData(
        appName: String,
        notificationPostedAt: NotificationPostedTime,
        notificationRemovedAt: NotificationRemovedTime
    ) {
        GlobalScope.launch {
            val uid = dependencyGraph.authentication.uid
            dependencyGraph.store.store(
                Store.Data(
                    uid,
                    appName,
                    notificationPostedAt,
                    notificationRemovedAt
                )
            )
        }
    }

}