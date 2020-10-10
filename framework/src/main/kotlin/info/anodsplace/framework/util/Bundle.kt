package info.anodsplace.framework.util

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