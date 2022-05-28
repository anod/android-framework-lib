package info.anodsplace.compose

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity

fun Context.findActivity(): ComponentActivity = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> throw IllegalStateException("Cannot find activity in context")
}