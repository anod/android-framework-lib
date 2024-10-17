package info.anodsplace.ktx

val Any?.isScalar: Boolean
    get() = when (this) {
                null -> true
                is String -> true
                is Boolean -> true
                is Byte -> true
                is Char -> true
                is Double -> true
                is Float -> true
                is Int -> true
                is Long -> true
                is Short -> true
                else -> false
            }

