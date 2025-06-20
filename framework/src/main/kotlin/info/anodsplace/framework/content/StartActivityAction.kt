package info.anodsplace.framework.content

import android.app.Activity
import android.content.Context
import android.content.Intent
import info.anodsplace.framework.app.addMultiWindowFlags
import info.anodsplace.framework.app.findActivity

interface StartActivityAction {
    val intent: Intent
}

fun Activity.startActivity(action: StartActivityAction) {
    startActivitySafely(action.intent)
}

fun Context.startActivity(action: StartActivityAction) {
    findActivity().startActivity(action)
}