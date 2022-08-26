package info.anodsplace.ktx

fun equalsHash(self: Any, other: Any?) : Boolean {
    if (self === other) return true
    if (other == null) return false
    if (self.javaClass != other.javaClass) return false
    return self.hashCode() == other.hashCode()
}

fun hashCodeOf(vararg input: Any?): Int {
    var hashCode = 1
    for (item in input) {
        hashCode = 31 * hashCode + when (item) {
            is Array<*> -> item.contentHashCode()
            else -> item.hashCode()
        }
    }
    return hashCode
}