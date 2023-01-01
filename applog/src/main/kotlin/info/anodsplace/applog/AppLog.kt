package info.anodsplace.applog

import android.os.Looper
import android.util.Log
import java.util.*
import java.util.regex.Pattern

/**
 * @author alex
 * @date 2015-05-11
 */
class AppLog {
    var listener: Listener? = null

    interface Logger {
        fun println(priority: Int, tag: String, msg: String)

        open class Android : Logger {
            override fun println(priority: Int, tag: String, msg: String) {
                Log.println(priority, tag, msg)
            }
        }

        open class StdOut : Logger {
            override fun println(priority: Int, tag: String, msg: String) {
                println("[$tag:$priority] $msg")
            }
        }
    }

    interface Listener {
        fun onLogException(tr: Throwable)
    }

    companion object {

        var tag = "AppLog"
        var level = Log.INFO
        val instance: AppLog by lazy { AppLog() }

        val isVerbose = level <= Log.VERBOSE
        val isDebug = level <= Log.DEBUG

        var logger: Logger = Logger.Android()

        fun setDebug(buildConfigDebug: Boolean, loggableTag: String) {
            val isDebug = buildConfigDebug || Log.isLoggable(loggableTag, Log.DEBUG)
            level = if (isDebug) {
                Log.DEBUG
            } else {
                Log.INFO
            }
        }

        fun d(msg: String) {
            log(Log.DEBUG, format(msg, null))
        }

        fun i(msg: String, tag: String = "") {
            log(Log.INFO, format(msg, tag))
        }

        fun v(msg: String) {
            log(Log.VERBOSE, format(msg, ""))
        }

        fun e(msg: String, tag: String?) {
            loge(format(msg, tag), null)
        }

        fun e(msg: String, tag: String, tr: Throwable) {
            loge(format(msg, tag), tr)
            instance.listener?.onLogException(tr)
        }

        fun e(msg: String, tr: Throwable) {
            loge(format(msg, null), tr)
            instance.listener?.onLogException(tr)
        }

        fun e(tr: Throwable) {
            val message = tr.message ?: "Throwable message is null"
            e(message, tr)
        }

        fun e(msg: String, tag: String, vararg params: Any) {
            loge(format(msg, tag, *params), null)
        }

        fun e(msg: String, vararg params: Any) {
            loge(format(msg, null, *params), null)
        }

        fun w(msg: String, tag: String? = null) {
            log(Log.WARN, format(msg, tag))
        }

        fun v(msg: String, vararg params: Any) {
            log(Log.VERBOSE, format(msg, "", *params))
        }

        private fun log(priority: Int, msg: String) {
            if (priority >= level) {
                logger.println(priority, tag, msg)
            }
        }

        private fun loge(message: String, tr: Throwable?) {
            val trace = if (logger is Logger.Android) Log.getStackTraceString(tr) else ""
            logger.println(Log.ERROR, tag, message + '\n'.toString() + trace)
        }

        private fun format(msg: String, tag: String?, vararg array: Any): String {
            val formatted: String = if (array.isEmpty()) {
                msg
            } else {
                try {
                    String.format(Locale.US, msg, *array)
                } catch (ex: IllegalFormatException) {
                    e("IllegalFormatException: formatString='%s' numArgs=%d", msg, array.size)
                    "$msg (An error occurred while formatting the message.)"
                }
            }

            val messageTag = if (tag == null) {
                var methodFromTrace = "<unknown>"
                val stackTrace = Throwable().fillInStackTrace().stackTrace
                for (i in 2 until stackTrace.size) {
                    val className = stackTrace[i].className
                    if (className != AppLog::class.java.name) {
                        val methodName = stackTrace[i].methodName
                        methodFromTrace = if (methodName == "invokeSuspend" || methodName == "invoke") {
                            createStackElementTag(stackTrace[i]) + ":" + stackTrace[i].lineNumber
                        } else {
                            createStackElementTag(stackTrace[i]).let {
                                if (it.isEmpty()) it else "$it."
                            } + stackTrace[i].methodName
                        }
                        break
                    }
                }
                methodFromTrace
            } else {
                tag
            }
            val isMain = Looper.myLooper() == Looper.getMainLooper()
            return String.format(Locale.US, "[%s%d] %s %s",
                    if (isMain) "MAIN:" else "",
                    Thread.currentThread().id,
                    messageTag,
                    formatted)
        }

        private val anonymousClass = Regex("(\\$\\d+)+$")

        private fun createStackElementTag(element: StackTraceElement): String {
            val tag = element.className
                .substringAfterLast('.')
                .replace(anonymousClass, "")

            val indexOfSign = tag.indexOf('$')
            val fileName = element.fileName?.replace(".", "")?.lowercase(Locale.ROOT)

            if (indexOfSign == -1) {
                if (tag.lowercase(Locale.ROOT) == fileName) {
                    return ""
                }
                return tag
            }

            return tag
                .replace("$fileName$", "", ignoreCase = true)
                .replace('$', '.')
        }
    }

}
