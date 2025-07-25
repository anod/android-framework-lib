package info.anodsplace.framework.content

import android.content.ComponentName
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import info.anodsplace.applog.AppLog
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import androidx.core.graphics.createBitmap

/**
 * @author alex
 * *
 * @date 9/18/13
 */

class InstalledPackage(val name: String, val versionCode: Int, val versionName: String, val updateTime: Long)

class InstalledPackageApp(val pkg: InstalledPackage, val title: String, val launchComponent: ComponentName?)

class AppTitleComparator(private val order: Int) : Comparator<InstalledPackageApp> {
    override fun compare(lPackage: InstalledPackageApp, rPackage: InstalledPackageApp): Int {
        return order * lPackage.title.compareTo(rPackage.title)
    }
}

private class AppUpdateTimePackageComparator(private val order: Int) : Comparator<InstalledPackage> {
    override fun compare(lPackage: InstalledPackage, rPackage: InstalledPackage): Int {
        return order * lPackage.updateTime.compareTo(rPackage.updateTime)
    }
}

class AppUpdateTimeComparator(private val order: Int) : Comparator<InstalledPackageApp> {
    override fun compare(lPackage: InstalledPackageApp, rPackage: InstalledPackageApp): Int {
        return order * lPackage.pkg.updateTime.compareTo(rPackage.pkg.updateTime)
    }
}

fun PackageManager.loadIcon(componentName: ComponentName, displayMetrics: DisplayMetrics): Bitmap? {
    var d: Drawable? = null
    try {
        d = this.getActivityIcon(componentName)
    } catch (_: PackageManager.NameNotFoundException) {
    }

    if (d == null) {
        try {
            d = this.getApplicationIcon(componentName.packageName)
        } catch (e1: PackageManager.NameNotFoundException) {
            AppLog.e(e1)
            return null
        }
    }

    if (d is BitmapDrawable) {
        // Ensure the bitmap has a density.
        val bitmapDrawable = d
        if (bitmapDrawable.bitmap.density == Bitmap.DENSITY_NONE) {
            bitmapDrawable.setTargetDensity(displayMetrics)
        }
        if (bitmapDrawable.bitmap.isRecycled) {
            AppLog.e("Bitmap is recycled for $componentName")
            return null
        }
        if (bitmapDrawable.bitmap.config == null) {
            AppLog.e("Bitmap config is null for $componentName")
            return null
        }
        // copy to avoid recycling problems
        return bitmapDrawable.bitmap.copy(bitmapDrawable.bitmap.config!!, true)
    }

    val bitmap = createBitmap(d.intrinsicWidth, d.intrinsicHeight)
    val canvas = Canvas(bitmap)
    d.setBounds(0, 0, canvas.width, canvas.height)
    d.draw(canvas)
    return bitmap
}

fun PackageManager.getAppTitle(packageName: String): String {
    val info = this.getPackageInfoOrNull(packageName) ?: return packageName
    return this.getAppTitle(info)
}

fun PackageManager.getAppUpdateTime(packageName: String): Long {
    val info = this.getPackageInfoOrNull(packageName) ?: return 0
    return info.lastUpdateTime
}

fun PackageManager.getInstalledPackagesCodes(): List<InstalledPackage> {
    val packs: List<PackageInfo>
    try {
        packs = this.getInstalledPackages(0)
    } catch (e: Exception) {
        AppLog.e(e)
        return getInstalledPackagesFallback()
    }

    val downloaded = ArrayList<InstalledPackage>(packs.size)
    for (packageInfo in packs) {
        val applicationInfo = packageInfo.applicationInfo ?: continue
        // Skips the system application (packages)
        if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1
                && applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP == 0) {
            continue
        }
        downloaded.add(InstalledPackage(packageInfo.packageName, packageInfo.versionCode, packageInfo.versionName
                ?: "", packageInfo.lastUpdateTime))
    }
    return downloaded
}

fun PackageManager.getInstalledApps(): List<InstalledPackageApp> {
    return getInstalledPackagesCodes().map {
        InstalledPackageApp(it, getAppTitle(it.name), getLaunchComponent(it.name))
    }
}

fun PackageManager.getRecentlyInstalled(): List<InstalledPackage> {
    return getInstalledPackagesCodes()
            .sortedWith(AppUpdateTimePackageComparator(-1))
}

private fun getInstalledPackagesFallback(): List<InstalledPackage> {
    val downloaded = ArrayList<InstalledPackage>()
    var bufferedReader: BufferedReader? = null
    try {
        val process = Runtime.getRuntime().exec("pm list packages")
        bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
        var line = bufferedReader.readLine()
        while (line != null) {
            val packageName = line.substring(line.indexOf(':') + 1)
            line = bufferedReader.readLine()
            downloaded.add(InstalledPackage(packageName, 0, "", -1))
        }
        process.waitFor()
    } catch (e: Exception) {
        AppLog.e(e)
    } finally {
        if (bufferedReader != null) {
            try {
                bufferedReader.close()
            } catch (e: IOException) {
                AppLog.e(e)
            }

        }
    }
    return downloaded
}

fun PackageManager.getAppTitle(info: PackageInfo): String {
    return info.applicationInfo?.loadLabel(this)?.toString() ?: info.packageName
}

fun PackageManager.getPackageInfoOrNull(packageName: String): PackageInfo? {
    var pkgInfo: PackageInfo? = null
    try {
        pkgInfo = this.getPackageInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        AppLog.e(e)
    }

    return pkgInfo
}

fun PackageManager.getLaunchComponent(packageName: String): ComponentName? {
    val launchIntent = this.getLaunchIntentForPackage(packageName)
    return launchIntent?.component
}

