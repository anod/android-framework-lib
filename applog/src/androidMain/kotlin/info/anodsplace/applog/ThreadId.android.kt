package info.anodsplace.applog

import android.os.Looper

actual fun threadName(): String = Thread.currentThread().id.toString()
actual fun isMainThread(): Boolean = Looper.getMainLooper() == Looper.myLooper()