package info.anodsplace.context

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

/**
 * @author Alex Gavrishev
 * @date 25/10/2017
 */

interface ApplicationInstance {
    fun sendBroadcast(intent: Intent)
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}

class ApplicationContext(context: Context) {

    constructor(application: Application) : this(application.applicationContext)

    val actual: Context = context.applicationContext as Context

    val resources: Resources
        get() = actual.resources
    val contentResolver: ContentResolver
        get() = actual.contentResolver

    fun getString(@StringRes resId: Int): String {
        return actual.getString(resId)
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return actual.getString(resId, *formatArgs)
    }

    @ColorInt
    fun getColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(actual, colorRes)
    }

    fun sendBroadcast(intent: Intent) {
        actual.sendBroadcast(intent)
    }
}