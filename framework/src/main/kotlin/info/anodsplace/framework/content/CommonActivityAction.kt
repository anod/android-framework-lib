package info.anodsplace.framework.content

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import info.anodsplace.framework.app.addMultiWindowFlags

sealed interface CommonActivityAction {
    object Finish : CommonActivityAction
    class ShowToast(@StringRes val resId: Int = 0, val text: String = "", val length: Int = Toast.LENGTH_SHORT) : CommonActivityAction
    class StartActivity(val intent: Intent, val addMultiWindowFlags: Boolean = false, val finish: Boolean = false) : CommonActivityAction
}

fun Activity.onCommonActivityAction(action: CommonActivityAction) {
    when (action) {
        is CommonActivityAction.ShowToast -> {
            if (action.resId == 0) {
                Toast.makeText(this, action.text, action.length).show()
            } else {
                Toast.makeText(this, action.resId, action.length).show()
            }
        }
        is CommonActivityAction.StartActivity -> {
            if (action.addMultiWindowFlags) {
                // TODO: Check if still used in AppWatcher
                startActivitySafely(action.intent.addMultiWindowFlags(this))
            } else {
                startActivitySafely(action.intent)
            }
            if (action.finish) {
                finish()
            }
        }
        CommonActivityAction.Finish -> finish()
    }
}