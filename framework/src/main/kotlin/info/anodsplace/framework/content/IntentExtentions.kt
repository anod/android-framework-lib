package info.anodsplace.framework.content

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.net.toUri
import info.anodsplace.applog.AppLog

fun Intent.forUninstall(packageName: String): Intent {
    this.action = Intent.ACTION_UNINSTALL_PACKAGE
    this.data = Uri.fromParts("package", packageName, null)
    return this
}

fun Intent.forAppInfo(packageName: String): Intent {
    this.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    this.data = Uri.fromParts("package", packageName, null)
    return this
}

fun Intent.forStoreSearch(query: String, category: String?): Intent {
    this.action = Intent.ACTION_VIEW
    this.data = "market://search?q=$query${category?.let { "&c=$it" }}".toUri()
    return this
}

fun Intent.forLauncher(): Intent {
    action = Intent.ACTION_MAIN
    addCategory(Intent.CATEGORY_LAUNCHER)
    return this
}

fun Intent.forHomeScreen(): Intent {
    action = Intent.ACTION_MAIN
    addCategory(Intent.CATEGORY_HOME)
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

fun Intent.forCustomImage(context: Context): Intent {
    val tempFile = context.getFileStreamPath("tempImage")
    action = Intent.ACTION_GET_CONTENT
    type = "image/*"
    putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
    putExtra("outputFormat", Bitmap.CompressFormat.PNG.name)
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

@SuppressLint("BatteryLife")
@RequiresPermission(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
fun Intent.forRequestIgnoreBatteryOptimization(packageName: String): Intent {
    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    data = Uri.fromParts("package", packageName, null)
    return this
}

fun Intent.forApplicationDetails(packageName: String): Intent {
    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    data = Uri.fromParts("package", packageName, null)
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
    return this
}

fun Intent.playStoreDetails(packageName: String): Intent {
    action = Intent.ACTION_VIEW
    data = Uri.parse("market://details?id=%s".format(packageName))
    flags = Intent.FLAG_ACTIVITY_NEW_TASK
    return this
}

fun Context.resolveDefaultCarDock(): String? {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_CAR_DOCK)
    }
    val info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return info?.activityInfo?.applicationInfo?.packageName ?: return null
}

fun Context.startActivitySafely(intent: Intent) {
    try {
        this.startActivity(intent)
    } catch (e: Exception) {
        AppLog.e(e)
        Toast.makeText(this, "Cannot start activity: $intent", Toast.LENGTH_SHORT).show()
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