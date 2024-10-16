package info.anodsplace.applog

import platform.Foundation.NSThread

actual fun threadName(): String = NSThread.currentThread.name ?: NSThread.currentThread.toString()
actual fun isMainThread(): Boolean = NSThread.isMainThread