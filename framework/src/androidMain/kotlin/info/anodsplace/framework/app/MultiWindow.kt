// Copyright (c) 2020. Alex Gavrishev
package info.anodsplace.framework.app

import android.app.Activity
import android.content.Context
import android.content.Intent

val Activity.isMultiWindow: Boolean
    get() = isInMultiWindowMode

val Context.isMultiWindow: Boolean
    get() = if (this is Activity) isMultiWindow else false

fun Intent.addMultiWindowFlags(context: Context): Intent {
    if (context.isMultiWindow) {
        addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or Intent.FLAG_ACTIVITY_NEW_TASK) // or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    }
    return this
}