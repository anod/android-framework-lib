package info.anodsplace.framework.app

import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.annotation.StringRes
import android.util.LruCache

/**
 * @author Alex Gavrishev
 * @date 25/10/2017
 */

interface ApplicationInstance {
    val notificationManager: NotificationManager
    val memoryCache: LruCache<String, Any?>
    val nightMode: Int

    fun sendBroadcast(intent: Intent)
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}

class ApplicationContext(context: Context) {
    val actual: Context = context.applicationContext as Context
    private val app: ApplicationInstance = context.applicationContext as ApplicationInstance

    val contentResolver: ContentResolver
        get() = actual.contentResolver
    val notificationManager: NotificationManager
        get() = app.notificationManager
    val memoryCache: LruCache<String, Any?>
        get() = app.memoryCache
    val nightMode: Int
        get() = app.nightMode

    fun getString(@StringRes resId: Int): String {
        return actual.getString(resId)
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return actual.getString(resId, *formatArgs)
    }

    val packageManager: PackageManager
        get() = actual.packageManager

    fun sendBroadcast(intent: Intent) {
        actual.sendBroadcast(intent)
    }

}