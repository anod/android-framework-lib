package info.anodsplace.framework.util

suspend fun <T, R> List<T>.chunked(
        action: suspend (items: List<T>) -> List<R>,
        chunkSize: Int = 998): List<R> {
    if (this.size <= chunkSize) {
        return action(this)
    }

    val chunks = this.chunked(chunkSize)
    val result = mutableListOf<R>()
    for (chunk in chunks) {
        val partial = action(chunk)
        result.addAll(partial)
    }
    return result
}