package info.anodsplace.framework.content

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import info.anodsplace.applog.AppLog

import info.anodsplace.framework.app.addMultiWindowFlags

fun Intent.forUninstall(packageName: String): Intent {
    this.action = Intent.ACTION_UNINSTALL_PACKAGE
    this.data = Uri.fromParts("package", packageName, null)
    return this
}

fun Intent.forAppInfo(packageName: String, context: Context): Intent {
    this.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    this.data = Uri.fromParts("package", packageName, null)
    addMultiWindowFlags(context)
    return this
}

fun Intent.forLauncher(): Intent {
    action = Intent.ACTION_MAIN
    addCategory(Intent.CATEGORY_LAUNCHER)
    return this
}

object IconPack {
    internal const val ACTION_ADW_PICK_ICON = "org.adw.launcher.icons.ACTION_PICK_ICON"
    internal const val THEME_CATEGORY = "com.anddoes.launcher.THEME"
}

fun Intent.forIconPack(): Intent {
    action = IconPack.ACTION_ADW_PICK_ICON
    return this
}

fun Intent.forIconTheme(): Intent {
    addCategory(IconPack.THEME_CATEGORY)
    action = Intent.ACTION_MAIN
    return this
}

fun Intent.forOverlayPermission(packageName: String): Intent {
    action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
    data = Uri.parse("package:$packageName")
    return this
}

fun Context.startActivitySafely(intent: Intent) {
    try {
        this.startActivity(intent)
    } catch (e: Exception) {
        AppLog.e(e)
        Toast.makeText(this, "Cannot start activity: $intent", Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.startActivitySafely(intent: Intent) {
    try {
        requireActivity().startActivity(intent)
    } catch (e: Exception) {
        AppLog.e(e)
        Toast.makeText(requireContext(), "Cannot start activity: $intent", Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.startActivityForResultSafely(intent: Intent, requestCode: Int) {
    try {
        this.startActivityForResult(intent, requestCode)
    } catch (e: Exception) {
        AppLog.e(e)
        Toast.makeText(requireContext(), "Cannot start activity: $intent", Toast.LENGTH_SHORT).show()
    }
}

fun Activity.startActivityForResultSafely(intent: Intent, requestCode: Int) {
    try {
        this.startActivityForResult(intent, requestCode)
    } catch (e: Exception) {
        AppLog.e(e)
        Toast.makeText(this, "Cannot start activity: $intent", Toast.LENGTH_SHORT).show()
    }
}