package info.anodsplace.compose

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import info.anodsplace.ktx.equalsHash
import info.anodsplace.ktx.hashCodeOf

interface CheckablePreferenceItem {
    var checked: Boolean
}

interface SingleValuePreferenceItem {
    var value: String
}

sealed class PreferenceItem{
    abstract val titleRes: Int
    abstract val title: String
    abstract val summaryRes: Int
    abstract val summary: String
    abstract val key: String
    abstract val enabled: Boolean
    abstract val clickable: Boolean

    data class Category(
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled
    ) : PreferenceItem()

    data class Text(
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled
    ) : PreferenceItem()
    data class Switch(
        override var checked: Boolean,
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled
    ): PreferenceItem(), CheckablePreferenceItem
    data class CheckBox(
        override var checked: Boolean,
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled
    ): PreferenceItem(), CheckablePreferenceItem
    data class List(
        @ArrayRes val entries: Int,
        @ArrayRes val entryValues: Int,
        override var value: String = "",
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled
    ): PreferenceItem(), SingleValuePreferenceItem
    data class Pick(
        @ArrayRes val entriesRes: Int = 0,
        @ArrayRes val entryValuesRes: Int = 0,
        val entries: Array<String> = emptyArray(),
        val entryValues: Array<String> = emptyArray(),
        override var value: String = "",
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled
    ): PreferenceItem(), SingleValuePreferenceItem {
        override fun equals(other: Any?) = equalsHash(this, other)
        override fun hashCode() = hashCodeOf(entriesRes, entryValuesRes, entries, entryValues, value,
                titleRes, title, summaryRes, summary, key, enabled)
    }

    data class Color(
        val color: androidx.compose.ui.graphics.Color?,
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled
    ) : PreferenceItem()

    data class Placeholder(
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = false
    ): PreferenceItem()
}

fun PreferenceItem.Placeholder.toTextItem() = PreferenceItem.Text(
    titleRes = titleRes,
    title = title,
    summaryRes = summaryRes,
    summary = summary,
    key = key
)

val PreferenceItem.checked: Boolean
    get() = (this as CheckablePreferenceItem).checked

val PreferenceItem.value: String
    get() = (this as SingleValuePreferenceItem).value