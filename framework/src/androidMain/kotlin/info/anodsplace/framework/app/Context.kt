package info.anodsplace.framework.app

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.activity.ComponentActivity

fun Context.findActivity(): ComponentActivity = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> throw IllegalStateException("Cannot find activity in context")
}

fun Context.findWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findWindow()
        else -> null
    }