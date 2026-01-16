package info.anodsplace.compose.chooser

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.collection.SimpleArrayMap
import info.anodsplace.applog.AppLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MediaListChooserLoader(context: Context) : ChooserLoader {
    private val packageManager = context.packageManager

    override fun load(): Flow<List<ChooserEntry>> = flow {
        emit(loadAll())
    }

    suspend fun loadAll(): List<ChooserEntry> = withContext(Dispatchers.Default) {
        val apps = packageManager
            .queryBroadcastReceivers(
                Intent(Intent.ACTION_MEDIA_BUTTON),
                PackageManager.GET_RESOLVED_FILTER
            )
        val receivers = SimpleArrayMap<String, Boolean>(apps.size)
        val list = mutableListOf<ChooserEntry>()
        for (appInfo in apps) {
            val pkg = appInfo.activityInfo.packageName
            if (sExcludePackages.contains(pkg) || receivers.containsKey(pkg)) {
                continue
            }
            val title = appInfo.activityInfo.applicationInfo.loadLabel(packageManager)
            if (AppLog.isDebug) {
                AppLog.d(appInfo.activityInfo.packageName + "/" + appInfo.activityInfo.applicationInfo.className)
            }
            receivers.put(pkg, true)
            val entry = ChooserEntry(appInfo, title.toString())
            list.add(entry)
        }
        list.sortWith(compareBy { it.title })
        return@withContext list
    }

    companion object {
        private var sExcludePackages = setOf(
            "com.amazon.kindle",
            "com.google.android.apps.magazines",
            "flipboard.app",
            "com.sec.android.app.storycam",
            "com.sec.android.app.mediasync",
            "com.sec.android.mmapp",
            "com.sec.android.automotive.drivelink",
            "com.sec.android.app.mv.player",
            "com.sec.android.app.voicenote",
            "com.sec.android.app.vepreload",
            "com.sec.android.app.ve.vebgm"
        )
    }
}