package info.anodsplace.ktx

import android.os.Bundle
import androidx.core.os.bundleOf

fun Bundle.putAny(key: String, value: Any?) {
    val newBundle = bundleOf(key to value)
    putAll(newBundle)
}

fun Bundle.put(key: String, value: String?, javaType: Class<out Any>) {
    when {
        value == null -> putString(key, null)
        javaType == Boolean::class.java -> putBoolean(key, value.toBoolean())
        javaType == Byte::class.java -> putByte(key, value.toByte())
        javaType == Char::class.java -> putChar(key, if (value.isNotEmpty()) value[0] else 0.toChar())
        javaType == Double::class.java -> putDouble(key, value.toDouble())
        javaType == Float::class.java -> putFloat(key, value.toFloat())
        javaType == Int::class.java -> putInt(key, value.toInt())
        javaType == Long::class.java -> putLong(key, value.toLong())
        javaType == Short::class.java -> putShort(key, value.toShort())
        else -> putString(key, value) // javaType == String::class.java ->
    }
}

/**
 * Safely copies a primitive value from this Bundle to the target Bundle.
 * Only copies safe primitive types (String, Int, Long, Boolean, Float, Double, CharSequence)
 * to avoid security issues with Parcelable, Serializable, or Binder objects.
 *
 * @param key The key to copy
 * @param target The target Bundle to copy to
 * @param onCopied Callback invoked when a value is successfully copied, receives the key and type name
 * @param onSkipped Callback invoked when a value is skipped (unsafe type or not found)
 * @return true if a value was copied, false otherwise
 */
fun Bundle.copySafePrimitive(
    key: String,
    target: Bundle,
    onCopied: (key: String, typeName: String) -> Unit = { _, _ -> },
    onSkipped: (key: String) -> Unit = { }
): Boolean {
    // Try String first (most common)
    val stringValue = getString(key)
    if (stringValue != null) {
        target.putString(key, stringValue)
        onCopied(key, "String")
        return true
    }

    // Try CharSequence
    val charSeqValue = getCharSequence(key)
    if (charSeqValue != null) {
        target.putCharSequence(key, charSeqValue)
        onCopied(key, "CharSequence")
        return true
    }

    // Try numeric primitives with default values using helper function
    if (tryGetAndCopy(key, target, onCopied, Int.MIN_VALUE, "Int", getter = { k, d -> getInt(k, d) }, setter = { k, v -> putInt(k, v) })) return true
    if (tryGetAndCopy(key, target, onCopied, Long.MIN_VALUE, "Long", getter = { k, d -> getLong(k, d) }, setter = { k, v -> putLong(k, v) })) return true
    if (tryGetAndCopy(key, target, onCopied, Float.MIN_VALUE, "Float", getter = { k, d -> getFloat(k, d) }, setter = { k, v -> putFloat(k, v) })) return true
    if (tryGetAndCopy(key, target, onCopied, Double.MIN_VALUE, "Double", getter = { k, d -> getDouble(k, d) }, setter = { k, v -> putDouble(k, v) })) return true

    // Try Boolean (no default value works reliably, so check containsKey)
    if (containsKey(key)) {
        try {
            val value = getBoolean(key)
            target.putBoolean(key, value)
            onCopied(key, "Boolean")
            return true
        } catch (_: Exception) {
            // Not a Boolean
        }
    }

    // If we get here, it's an unsafe type or doesn't exist
    onSkipped(key)
    return false
}

/**
 * Helper function to try getting a value with a default, and copy it if it exists.
 * This is a small "getValue"-style abstraction to reduce duplication.
 */
private inline fun <T> Bundle.tryGetAndCopy(
    key: String,
    target: Bundle,
    onCopied: (key: String, typeName: String) -> Unit,
    defaultValue: T,
    typeName: String,
    crossinline getter: Bundle.(key: String, defaultValue: T) -> T,
    crossinline setter: Bundle.(key: String, value: T) -> Unit,
): Boolean {
    return try {
        val value = getter(key, defaultValue)
        if (value != defaultValue || containsKey(key)) {
            target.setter(key, value)
            onCopied(key, typeName)
            true
        } else {
            false
        }
    } catch (_: Exception) {
        false
    }
}

/**
 * Creates a new Bundle containing only safe primitive values from this Bundle.
 * Only copies safe primitive types (String, Int, Long, Boolean, Float, Double, CharSequence)
 * to avoid security issues with Parcelable, Serializable, or Binder objects.
 *
 * @param onCopied Callback invoked for each value successfully copied, receives the key and type name
 * @param onSkipped Callback invoked for each value skipped (unsafe type), receives the key
 * @return A new Bundle containing only safe primitive values
 */
fun Bundle.copySafePrimitives(
    onCopied: (key: String, typeName: String) -> Unit = { _, _ -> },
    onSkipped: (key: String) -> Unit = { }
): Bundle {
    val result = Bundle()

    for (key in keySet()) {
        copySafePrimitive(
            key = key,
            target = result,
            onCopied = onCopied,
            onSkipped = onSkipped
        )
    }

    return result
}
