package info.anodsplace.framework.content

import android.content.pm.PackageInfo
import androidx.collection.ArrayMap

/**
 * @author Alex Gavrishev
 * @date 03/03/2017.
 */
interface InstalledApps {

    fun packageInfo(packageName: String): Info

    class Info(val versionCode: Int, val versionName: String) {

        fun isUpdatable(versionNumber: Int): Boolean {
            return this.versionCode in 1 until versionNumber
        }

        val isInstalled: Boolean
            get() = this.versionCode > 0
    }

    class PackageManager(private val packageManager: android.content.pm.PackageManager) : InstalledApps {

        override fun packageInfo(packageName: String): Info {
            var pkgInfo: PackageInfo? = null
            try {
                pkgInfo = packageManager.getPackageInfo(packageName, 0)
            } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
                // skip
            }

            if (pkgInfo != null) {
                val versionName = pkgInfo.versionName ?: ""
                return Info(pkgInfo.versionCode, versionName)
            }
            return Info(0, "")
        }

    }

    class MemoryCache(private val installedApps: InstalledApps) : InstalledApps {
        private val cache = ArrayMap<String, Info>()

        override fun packageInfo(packageName: String): Info {
            if (cache.containsKey(packageName)) {
                return cache[packageName]!!
            }

            val info = installedApps.packageInfo(packageName)
            cache[packageName] = info
            return info
        }

        fun reset() {
            cache.clear()
        }

    }

}
