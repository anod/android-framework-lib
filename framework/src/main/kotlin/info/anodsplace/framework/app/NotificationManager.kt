package info.anodsplace.framework.app

import android.app.Notification
import android.app.NotificationChannel
import androidx.core.app.NotificationManagerCompat

interface NotificationManager {
    val areNotificationsEnabled: Boolean

    fun createNotificationChannels(channels: List<NotificationChannel>) {}
    fun notify(notificationId: Int, notification: Notification) {}
    fun cancel(notificationId: Int) {}

    class NoOp(override val areNotificationsEnabled: Boolean = false) : NotificationManager

    companion object {
        const val IMPORTANCE_DEFAULT = NotificationManagerCompat.IMPORTANCE_DEFAULT
    }
}

class RealNotificationManager(private val context: ApplicationContext) : NotificationManager {
    private val compat = NotificationManagerCompat.from(context.actual)

    override val areNotificationsEnabled: Boolean
        get() = compat.areNotificationsEnabled()

    override fun createNotificationChannels(channels: List<NotificationChannel>) {
        compat.createNotificationChannels(channels)
    }

    override fun notify(notificationId: Int, notification: Notification) {
        compat.notify(notificationId, notification)
    }

    override fun cancel(notificationId: Int) {
        compat.cancel(notificationId)
    }
}