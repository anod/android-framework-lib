package info.anodsplace.compose

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import info.anodsplace.ktx.equalsHash
import info.anodsplace.ktx.hashCodeOf

interface CheckablePreferenceItem : ActionablePreferenceItem{
    var checked: Boolean
}

interface SingleValuePreferenceItem : ActionablePreferenceItem {
    var value: String
}

interface ActionablePreferenceItem {
    @get:StringRes
    val titleRes: Int
    val title: String
    @get:StringRes
    val summaryRes: Int
    val summary: String
    val key: String
    val enabled: Boolean
    val clickable: Boolean
}

sealed class PreferenceItem {
    data class Spacer(val height: Dp = 8.dp, ) : PreferenceItem()
    data class Category(
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled,
        val capitalize: Boolean = true
    ) : PreferenceItem(), ActionablePreferenceItem
    data class Text(
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled
    ) : PreferenceItem(), ActionablePreferenceItem
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
    ) : PreferenceItem(), ActionablePreferenceItem

    data class Placeholder(
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = false
    ): PreferenceItem(), ActionablePreferenceItem
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

val PreferenceItem.enabled: Boolean
    get() = (this as ActionablePreferenceItem).enabled

val PreferenceItem.clickable: Boolean
    get() = (this as ActionablePreferenceItem).clickable

val PreferenceItem.titleRes: Int
    get() = (this as ActionablePreferenceItem).titleRes

val PreferenceItem.title: String
    get() = (this as ActionablePreferenceItem).title

val PreferenceItem.summaryRes: Int
    get() = (this as ActionablePreferenceItem).summaryRes

val PreferenceItem.summary: String
    get() = (this as ActionablePreferenceItem).summary

val PreferenceItem.key: String
    get() = (this as ActionablePreferenceItem).key