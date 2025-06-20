package info.anodsplace.framework.content

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

interface ShowToastAction {
    @get:StringRes
    val resId: Int
    val text: String
    val length: Int
}

open class ShowToastActionDefaults(
    @get:StringRes
    override val resId: Int = 0,
    override val text: String = "",
    override val length: Int = Toast.LENGTH_LONG
) : ShowToastAction

fun Context.showToast(action: ShowToastAction) {
    if (action.resId == 0) {
        Toast.makeText(this, action.text, action.length).show()
    } else {
        Toast.makeText(this, action.resId, action.length).show()
    }
}