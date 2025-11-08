package info.anodsplace.framework.content

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes

interface ScreenCommonNavKey

interface ShowSnackbarData

interface ShowDialogData

fun startActivityAction(intent: Intent): ScreenCommonAction {
    return ScreenCommonAction.StartActivity(intent)
}

fun showToastAction(
    @StringRes resId: Int = 0,
    text: String = "",
    length: Int = Toast.LENGTH_SHORT
): ScreenCommonAction {
    return ScreenCommonAction.ShowToast(resId, text, length)
}

sealed interface ScreenCommonAction {
    data class StartActivity(override val intent: Intent) : ScreenCommonAction, StartActivityAction
    class ShowToast(@StringRes resId: Int = 0, text: String = "", length: Int = Toast.LENGTH_SHORT) : ShowToastActionDefaults(resId, text, length), ScreenCommonAction
    object NavigateBack : ScreenCommonAction
    data class NavigateTo(val navKey: ScreenCommonNavKey) : ScreenCommonAction
    data class ShowSnackbar(val data: ShowSnackbarData) : ScreenCommonAction
    data class ShowDialog(val data: ShowDialogData) : ScreenCommonAction
}

fun Context.onScreenCommonAction(
    action: ScreenCommonAction,
    navigateBack: () -> Unit,
    navigateTo: (ScreenCommonNavKey) -> Unit,
    showSnackbar: (ShowSnackbarData) -> Unit = {},
    showDialog: (ShowDialogData) -> Unit = {},
) {
    when (action) {
        ScreenCommonAction.NavigateBack -> navigateBack()
        is ScreenCommonAction.NavigateTo -> navigateTo(action.navKey)
        is ScreenCommonAction.ShowSnackbar -> showSnackbar(action.data)
        is ScreenCommonAction.ShowDialog -> showDialog(action.data)
        is ScreenCommonAction.ShowToast -> showToast(action)
        is ScreenCommonAction.StartActivity -> startActivity(action)
    }
}