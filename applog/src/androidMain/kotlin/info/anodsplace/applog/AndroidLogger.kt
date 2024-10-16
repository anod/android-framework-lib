package info.anodsplace.applog

import android.util.Log

 open class AndroidLogger : AppLog.Logger {
     override fun isLoggable(tag: String, level: Int): Boolean = Log.isLoggable(tag, level)
     override fun getStackTraceString(tr: Throwable?): String = Log.getStackTraceString(tr)
     override fun println(priority: Int, tag: String, msg: String) {
         Log.println(priority, tag, msg)
     }
 }
