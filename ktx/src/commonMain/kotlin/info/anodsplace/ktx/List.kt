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