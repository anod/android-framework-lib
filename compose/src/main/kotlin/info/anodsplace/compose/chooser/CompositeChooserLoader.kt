package info.anodsplace.compose.chooser

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import java.text.Collator
import java.util.Locale

/**
 * Loader that combines results of multiple [ChooserLoader] instances.
 * For duplicate entries (same componentName when present, otherwise same title) the entry
 * from the loader appearing earlier in the [loaders] list wins.
 * Each emitted [ChooserEntry] will have its [ChooserEntry.sourceLoader] set to the index of
 * the originating loader (if it was not already set or set to -1).
 */
class CompositeChooserLoader(private val loaders: List<ChooserLoader>) : ChooserLoader {
    private val collator: Collator = Collator.getInstance(Locale.getDefault()).apply { strength = Collator.PRIMARY }

    override fun load(): Flow<List<ChooserEntry>> {
        if (loaders.isEmpty()) return flowOf(emptyList())
        val flows = loaders.map { it.load() }
        return combine(flows) { lists: Array<out List<ChooserEntry>> ->
            val map = LinkedHashMap<String, ChooserEntry>()
            // Iterate in loader order to ensure first occurrence kept
            for (loaderIndex in lists.indices) {
                val entries = lists[loaderIndex]
                for (entry in entries) {
                    val key = entry.key()
                    if (!map.containsKey(key)) {
                        if (entry.sourceLoader == -1) entry.sourceLoader = loaderIndex
                        map[key] = entry
                    }
                }
            }
            // Sort by localized title for stable ordering matching other loaders
            map.values.sortedWith { a, b -> collator.compare(a.title, b.title) }
        }
    }

    private fun ChooserEntry.key(): String = when {
        componentName != null -> componentName.flattenToShortString()
        intent?.component != null -> "i:${intent!!.component!!.flattenToShortString()}"
        title.isNotEmpty() -> "t:$title"
        else -> hashCode().toString()
    }
}