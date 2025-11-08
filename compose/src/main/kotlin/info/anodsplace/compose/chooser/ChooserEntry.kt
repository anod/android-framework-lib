package info.anodsplace.compose.chooser

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import info.anodsplace.applog.AppLog

fun headerEntry(
    headerId: Int,
    title: String,
    iconVector: ImageVector? = null,
    iconRes: Int = 0,
    intent: Intent? = null
): ChooserEntry =
    ChooserEntry(null, title, iconRes = iconRes, iconVector = iconVector, intent = intent).apply {
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

data class ChooserEntry(
    val componentName: ComponentName?,
    var title: String,
    @param:DrawableRes
    val iconRes: Int = 0,
    val icon: Drawable? = null,
    val iconVector: ImageVector? = null,
    var intent: Intent? = null,
    var extras: Bundle? = null,
) {
    companion object {
        private const val KEY_CATEGORY = "category"
        private const val KEY_SOURCE_LOADER = "sourceLoader"
        private const val KEY_SOURCE_SHORTCUT_ID = "sourceShortcutId"
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

    var sourceShortcutId: Long
        get() = extras?.getLong(KEY_SOURCE_SHORTCUT_ID, -1) ?: -1
        set(value) {
            ensureExtras().putLong(KEY_SOURCE_SHORTCUT_ID, value)
        }

    constructor(
        componentName: ComponentName?,
        title: String,
        icon: Drawable?,
        category: Int,
    ) : this(componentName, title, icon = icon) {
        this.category = category
    }

    constructor(info: ResolveInfo, title: String?) :
        this(
            componentName = ComponentName(
                info.activityInfo.applicationInfo.packageName,
                info.activityInfo.name
            ),
            title = title ?: info.activityInfo.name ?: "",
            icon = null,
            category = info.category()
        )

    constructor(title: String, icon: Drawable?)
            : this(componentName = null, title = title, icon = icon)

    constructor(pm: PackageManager, resolveInfo: ResolveInfo)
            : this(
        componentName = ComponentName(
            resolveInfo.activityInfo.applicationInfo.packageName,
            resolveInfo.activityInfo.name
        ),
        title = resolveInfo.loadLabel(pm).toString(),
        icon = resolveInfo.loadIcon(pm),
        category = resolveInfo.category()
    )

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

