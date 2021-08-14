package info.anodsplace.ktx

fun hashCodeOf(vararg input: Any?): Int {
    var hashCode = 1
    for (item in input) {
        hashCode = 31 * hashCode + item.hashCode()
    }
    return hashCode
}
