package info.anodsplace.applog

/**
 * @author alex
 * @date 2015-05-11
 */
class AppLog {
    var listener: Listener? = null

    interface Logger {
        fun isLoggable(tag: String, level: Int): Boolean = true
        fun println(priority: Int, tag: String, msg: String)
        fun getStackTraceString(tr: Throwable?): String = ""

        open class StdOut : Logger {
            override fun println(priority: Int, tag: String, msg: String) {
                println("[$tag:$priority] $msg")
            }
        }
    }

    interface Listener {
        fun onLogException(tr: Throwable)
    }

    enum class Level(val priority: Int) {
        VERBOSE(2),
        DEBUG(3),
        INFO(4),
        WARN(5),
        ERROR(6),
        ASSERT(7);
    }

    companion object {
        var tag = "AppLog"
        var level = Level.INFO.priority
        val instance: AppLog by lazy { AppLog() }

        val isVerbose = level <= Level.VERBOSE.priority
        val isDebug = level <= Level.DEBUG.priority

        var logger: Logger = Logger.StdOut()

        fun setDebug(buildConfigDebug: Boolean, loggableTag: String) {
            val isDebug = buildConfigDebug || logger.isLoggable(loggableTag, Level.DEBUG.priority)
            level = if (isDebug) {
                Level.DEBUG.priority
            } else {
                Level.INFO.priority
            }
        }

        fun d(msg: String) {
            log(Level.DEBUG.priority, format(msg, null))
        }

        fun i(msg: String, tag: String = "") {
            log(Level.INFO.priority, format(msg, tag))
        }

        fun v(msg: String) {
            log(Level.VERBOSE.priority, format(msg, ""))
        }

        fun e(msg: String, tag: String?) {
            loge(format(msg, tag), tr =null)
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

        fun e(msg: String) {
            loge(format(msg, null), null)
        }

        fun w(msg: String, tag: String? = null) {
            log(Level.WARN.priority, format(msg, tag))
        }

        private fun log(priority: Int, msg: String) {
            if (priority >= level) {
                logger.println(priority, tag, msg)
            }
        }

        private fun loge(message: String, tr: Throwable?) {
            val trace = logger.getStackTraceString(tr)
            logger.println(Level.ERROR.priority, tag, message + '\n'.toString() + trace)
        }

        private fun format(msg: String, tag: String?): String {
            val messageTag = if (tag.isNullOrEmpty()) "<unk>" else " $tag"
            return "${if (isMainThread()) "MAIN" else threadName()} $messageTag $msg"
        }
    }

}
