package info.anodsplace.ktx

suspend fun <T, R> List<T>.chunked(
    action: suspend (items: List<T>) -> List<R>,
    chunkSize: Int = 998
): List<R> {
    if (this.size <= chunkSize) {
        return action(this)
    }

    return this.chunked(chunkSize).flatMap { chunk -> action(chunk) }
}

/**
 * Overloaded non-mutating parallel sort that builds a list of keys using [keySelector] applied to
 * elements of the receiver (primary) list, sorts by those keys, and returns a Pair of:
 *  - first: sorted list of non-null keys
 *  - second: elements from [companion] reordered to match the sorted key order
 *
 * Only elements where [keySelector] returns non-null are included (others skipped in both results).
 * Uses an in-place insertion sort over the collected arrays to minimize allocations (O(n^2) but n is small here).
 */
inline fun <T, U, K : Comparable<K>> List<T>.sortAndReorder(
    companion: List<U>,
    crossinline keySelector: (T) -> K?
): Pair<List<K>, List<U>> {
    require(size == companion.size) { "Lists must be same size: primary=$size companion=${companion.size}" }
    if (isEmpty()) return emptyList<K>() to emptyList()

    // First pass: count non-null keys
    var count = 0
    for (i in indices) if (keySelector(this[i]) != null) count++
    if (count == 0) return emptyList<K>() to emptyList()

    // Collect keys + values (only non-null keys)
    val keys = ArrayList<K>(count)
    val values = ArrayList<U>(count)
    for (i in indices) {
        val k = keySelector(this[i]) ?: continue
        keys += k
        values += companion[i]
    }

    // Insertion sort on keys while moving values in parallel (stable, minimal temp objects)
    for (i in 1 until keys.size) {
        val k = keys[i]
        val v = values[i]
        var j = i - 1
        while (j >= 0 && keys[j] > k) {
            keys[j + 1] = keys[j]
            values[j + 1] = values[j]
            j--
        }
        if (j + 1 != i) {
            keys[j + 1] = k
            values[j + 1] = v
        }
    }
    return keys to values
}
