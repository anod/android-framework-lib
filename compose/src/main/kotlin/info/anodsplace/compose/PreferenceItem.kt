package info.anodsplace.compose

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import info.anodsplace.ktx.equalsHash
import info.anodsplace.ktx.hashCodeOf
import java.util.UUID

interface CheckablePreferenceItem : ActionablePreferenceItem{
    val checked: Boolean
}

interface SingleValuePreferenceItem : ActionablePreferenceItem {
    val value: String
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
    val stableKey: String = UUID.randomUUID().toString()
    abstract val contentType: String

    @Immutable
    data class Spacer(val height: Dp = 8.dp, override val contentType: String = "Spacer") : PreferenceItem()

    @Immutable
    data class Category(
        @param:StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @param:StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled,
        val capitalize: Boolean = true,
        override val contentType: String = "Category",
    ) : PreferenceItem(), ActionablePreferenceItem {
        override fun equals(other: Any?) = equalsHash(this, other)
        override fun hashCode() = hashCode
    }

    @Immutable
    data class Text(
        @param:StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @param:StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled,
        override val contentType: String = "Text",
    ) : PreferenceItem(), ActionablePreferenceItem {
        override fun equals(other: Any?) = equalsHash(this, other)
        override fun hashCode() = hashCode
    }

    @Immutable
    data class Switch(
        override val checked: Boolean,
        @param:StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @param:StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled,
        override val contentType: String = "Switch",
    ) : PreferenceItem(), CheckablePreferenceItem {
        override fun equals(other: Any?) = equalsHash(this, other)
        override fun hashCode() = hashCode
    }

    @Immutable
    data class CheckBox(
        override val checked: Boolean,
        @param:StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @param:StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled,
        override val contentType: String = "CheckBox",
    ) : PreferenceItem(), CheckablePreferenceItem {
        override fun equals(other: Any?) = equalsHash(this, other)
        override fun hashCode() = hashCode
    }

    @Immutable
    data class List(
        @param:ArrayRes val entries: Int,
        @param:ArrayRes val entryValues: Int,
        override val value: String = "",
        @param:StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @param:StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled,
        override val contentType: String = "List",
    ) : PreferenceItem(), SingleValuePreferenceItem {
        override fun equals(other: Any?) = equalsHash(this, other)
        override fun hashCode() = hashCodeOf(
            entries, entryValues, entries, entryValues, value,
            titleRes, title, summaryRes, summary, key, enabled
        )
    }

    @Immutable
    data class Pick(
        @param:ArrayRes val entriesRes: Int = 0,
        @param:ArrayRes val entryValuesRes: Int = 0,
        val entries: Array<String> = emptyArray(),
        val entryValues: Array<String> = emptyArray(),
        override val value: String = "",
        @param:StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @param:StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled,
        override val contentType: String = "Pick",
    ): PreferenceItem(), SingleValuePreferenceItem {
        override fun equals(other: Any?) = equalsHash(this, other)
        override fun hashCode() = hashCodeOf(
            entriesRes, entryValuesRes, entries, entryValues, value,
            titleRes, title, summaryRes, summary, key, enabled
        )
    }

    @Immutable
    data class Color(
        val color: androidx.compose.ui.graphics.Color?,
        @param:StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @param:StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = enabled,
        override val contentType: String = "Color",
    ) : PreferenceItem(), ActionablePreferenceItem {
        override fun equals(other: Any?) = equalsHash(this, other)
        override fun hashCode() = hashCodeOf(color, titleRes, title, summaryRes, summary, key, enabled)
    }

    @Immutable
    data class Placeholder(
        @param:StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @param:StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true,
        override val clickable: Boolean = false,
        override val contentType: String = "Placeholder",
    ) : PreferenceItem(), ActionablePreferenceItem {
        override fun equals(other: Any?) = equalsHash(this, other)
        override fun hashCode() = hashCode
    }
}

fun PreferenceItem.Placeholder.toTextItem() = PreferenceItem.Text(
    titleRes = titleRes,
    title = title,
    summaryRes = summaryRes,
    summary = summary,
    key = key
)

val ActionablePreferenceItem.hashCode: Int
    get() = hashCodeOf(titleRes, title, summaryRes, summary, key, enabled, clickable)

val SingleValuePreferenceItem.hashCode: Int
    get() = hashCodeOf(value, titleRes, title, summaryRes, summary, key, enabled, clickable)

val CheckablePreferenceItem.hashCode: Int
    get() = hashCodeOf(titleRes, title, summaryRes, summary, key, enabled, clickable, checked)

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