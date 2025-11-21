package info.anodsplace.compose.chooser

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext


open class QueryIntentChooserLoader(context: Context, private val queryIntent: Intent, private val includeSelfPackage: Boolean = false) :
    ChooserLoader {

    private val packageManager = context.packageManager
    private val selfPackage = context.packageName
    private val mediaListLoader = MediaListChooserLoader(context)

    override fun load(): Flow<List<ChooserEntry>> = flow {
        emit(loadAll())
    }

    private suspend fun loadAll(): List<ChooserEntry> = withContext(Dispatchers.Default) {
        val list = mutableListOf<ChooserEntry>()

        val audioPkgs = mediaListLoader.loadAll().mapNotNull { entry -> entry.componentName?.packageName }.toSet()

        // Request resolved filters so we can inspect intent categories (minimal overhead)
        val apps = packageManager.queryIntentActivities(queryIntent, PackageManager.GET_RESOLVED_FILTER)
        for (appInfo in apps) {
            val pkg = appInfo.activityInfo.packageName
            if ((includeSelfPackage || !pkg.startsWith(selfPackage)) && appInfo.activityInfo.exported) {
                val title = appInfo.activityInfo.loadLabel(packageManager).toString()
                var category = appInfo.category()
                if (category != ApplicationInfo.CATEGORY_AUDIO && audioPkgs.contains(pkg)) {
                    category = ApplicationInfo.CATEGORY_AUDIO
                }
                val entry = ChooserEntry(
                    componentName = ComponentName(pkg, appInfo.activityInfo.name),
                    title = title,
                    category = category
                )
                list.add(entry)
            }
        }

        list.sortWith(compareBy { it.title })
        return@withContext list
    }
}