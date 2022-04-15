package info.anodsplace.compose

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import info.anodsplace.ktx.equalsHash
import info.anodsplace.ktx.hashCodeOf

sealed class PreferenceItem{
    abstract val titleRes: Int
    abstract val title: String
    abstract val summaryRes: Int
    abstract val summary: String
    abstract val key: String
    abstract val enabled: Boolean

    data class Category(
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true
    ) : PreferenceItem()

    data class Text(
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true
    ) : PreferenceItem()
    data class Switch(
        var checked: Boolean,
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true
    ): PreferenceItem()
    data class CheckBox(
        var checked: Boolean,
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true
    ): PreferenceItem()
    data class List(
        @ArrayRes val entries: Int,
        @ArrayRes val entryValues: Int,
        var value: String = "",
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true
    ): PreferenceItem()
    data class Pick(
            @ArrayRes val entriesRes: Int = 0,
            @ArrayRes val entryValuesRes: Int = 0,
            val entries: Array<String> = emptyArray(),
            val entryValues: Array<String> = emptyArray(),
            var value: String = "",
            @StringRes override val titleRes: Int = 0,
            override val title: String = "",
            @StringRes override val summaryRes: Int = 0,
            override val summary: String = "",
            override val key: String = "",
            override val enabled: Boolean = true
    ): PreferenceItem() {
        override fun equals(other: Any?) = equalsHash(this, other)
        override fun hashCode() = hashCodeOf(entriesRes, entryValuesRes, entries, entryValues, value,
                titleRes, title, summaryRes, summary, key, enabled)
    }

    data class Placeholder(
        @StringRes override val titleRes: Int = 0,
        override val title: String = "",
        @StringRes override val summaryRes: Int = 0,
        override val summary: String = "",
        override val key: String = "",
        override val enabled: Boolean = true
    ): PreferenceItem()
}

fun PreferenceItem.Placeholder.toTextItem() = PreferenceItem.Text(
    titleRes = titleRes,
    title = title,
    summaryRes = summaryRes,
    summary = summary,
    key = key
)