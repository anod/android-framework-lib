package info.anodsplace.compose.chooser

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import info.anodsplace.applog.AppLog
import info.anodsplace.ktx.appIconUri
import info.anodsplace.ktx.resourceUri

fun headerEntry(
    headerId: Int,
    title: String,
    iconVector: ImageVector? = null,
    iconUri: Uri? = null,
    intent: Intent? = null
): ChooserEntry =
    ChooserEntry(null, title, iconUri = iconUri, iconVector = iconVector, intent = intent).apply {
        this.headerId = headerId
    }

var ChooserEntry.headerId: Int
    get() = extras?.getInt("headerId", 0) ?: 0
    set(value) = ensureExtras().putInt("headerId", value)

val ChooserEntry.isHeader: Boolean
    get() = extras?.containsKey("headerId") == true

fun sectionEntry(sectionId: String, title: String): ChooserEntry = ChooserEntry(null, title).apply {
    this.sectionId = sectionId
}

var ChooserEntry.sectionId: String?
    get() = extras?.getString("sectionId", null)
    set(value) = ensureExtras().putString("sectionId", value)

val ChooserEntry.isSection: Boolean
    get() = extras?.containsKey("sectionId") == true

@Stable
data class ChooserEntry(
    val componentName: ComponentName?,
    var title: String,
    val iconUri: Uri? = componentName?.appIconUri,
    val iconVector: ImageVector? = null,
    var intent: Intent? = null,
    var extras: Bundle? = null,
) {
    companion object {
        private const val KEY_CATEGORY = "category"
        private const val KEY_SOURCE_LOADER = "sourceLoader"
    }

    var category: Int
        get() = extras?.getInt(KEY_CATEGORY, ApplicationInfo.CATEGORY_UNDEFINED)
            ?: ApplicationInfo.CATEGORY_UNDEFINED
        set(value) = ensureExtras().putInt(KEY_CATEGORY, value)

    var sourceLoader: Int
        get() = extras?.getInt(KEY_SOURCE_LOADER, -1) ?: -1
        set(value) {
            ensureExtras().putInt(KEY_SOURCE_LOADER, value)
        }

    constructor(
        componentName: ComponentName?,
        title: String,
        category: Int,
    ) : this(componentName, title) {
        this.category = category
    }

    constructor(
        context: Context,
        title: String,
        iconRes: Int,
        extras: Bundle? = null,
    ) : this(
        componentName = null,
        title = title,
        iconUri = context.resourceUri(iconRes),
        extras = extras
    )

    constructor(info: ResolveInfo, title: String?) :
        this(
            componentName = ComponentName(
                info.activityInfo.applicationInfo.packageName,
                info.activityInfo.name
            ),
            title = title ?: info.activityInfo.name ?: "",
            category = info.category()
        )

    constructor(title: String, icon: Drawable?)
            : this(componentName = null, title = title)

    /**
     * Build the [Intent] described by this item. If this item
     * can't create a valid [ComponentName], it
     * will return [Intent.ACTION_CREATE_SHORTCUT] filled with the
     * item label.
     */
    fun getIntent(baseIntent: Intent?): Intent {
        if (this.intent != null) {
            return this.intent!!
        }
        val intent = if (baseIntent != null) {
            Intent(baseIntent)
        } else {
            Intent(Intent.ACTION_MAIN)
        }
        if (componentName != null) {
            // Valid package and class, so fill details as normal intent
            intent.component = componentName
            if (extras != null) {
                intent.putExtras(extras!!)
            }
        } else {
            intent.action = Intent.ACTION_CREATE_SHORTCUT
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title)
        }
        return intent
    }

    fun ensureExtras(): Bundle {
        if (extras == null) extras = Bundle()
        return extras!!
    }
}

// Lightweight fallback derivation; O(1) per app (small iteration over intent filter categories)
fun ResolveInfo.category(): Int {
    val manifestCategory = activityInfo.applicationInfo.category
    val filter = this.filter ?: return manifestCategory

    // Additional heuristic: any receiver/activity handling ACTION_MEDIA_BUTTON is considered AUDIO.
    // This lets us classify audio apps even if they don't declare CATEGORY_APP_MUSIC on an activity.
    try {
        if (filter.hasAction(Intent.ACTION_MEDIA_BUTTON)) {
            return ApplicationInfo.CATEGORY_AUDIO
        }
    } catch (_: Exception) {
        // Ignore – fall back to manifest or category iteration
    }

    val it = try {
        filter.categoriesIterator()
    } catch (_: Exception) {
        null
    } ?: return manifestCategory
    while (it.hasNext()) {
        when (val cat = it.next()) {
            Intent.CATEGORY_APP_MUSIC -> return ApplicationInfo.CATEGORY_AUDIO
            Intent.CATEGORY_APP_MAPS -> return ApplicationInfo.CATEGORY_MAPS
            Intent.CATEGORY_APP_BROWSER -> return ApplicationInfo.CATEGORY_PRODUCTIVITY
            Intent.CATEGORY_APP_EMAIL -> return ApplicationInfo.CATEGORY_SOCIAL
            Intent.CATEGORY_APP_GALLERY -> return ApplicationInfo.CATEGORY_IMAGE
            Intent.CATEGORY_APP_MESSAGING -> return ApplicationInfo.CATEGORY_SOCIAL
            // Ignore other categories
            else -> if (AppLog.isDebug) {
                // Only log rarely; not performance critical
                AppLog.v("Unhandled intent category $cat for ${activityInfo.packageName}")
            }
        }
    }
    return manifestCategory
}

