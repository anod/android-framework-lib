package info.anodsplace.android.log;

import android.support.annotation.Nullable;
import android.util.Log;


import java.util.IllegalFormatException;
import java.util.Locale;


/**
 * @author alex
 * @date 2015-05-11
 */
public class AppLog {

    private static final String TAG = "AppLog";
    private static int LOG_LEVEL = Log.INFO;
    private static volatile AppLog singleton = null;

    public static final boolean LOG_VERBOSE = LOG_LEVEL <= Log.VERBOSE;
    public static final boolean LOG_DEBUG = LOG_LEVEL <= Log.DEBUG;

    public static Logger LOGGER = new Logger.Android();

    public interface Logger
    {
        void println(int priority, String tag, String msg);

        class Android implements Logger
        {
            @Override
            public void println(int priority, String tag, String msg) {
                Log.println(priority, TAG, msg);
            }
        }

        class StdOut implements Logger
        {
            @Override
            public void println(int priority, String tag, String msg) {
                System.out.println("["+tag+":"+priority+"] " + msg);
            }
        }
    }

    private Listener mListener;

    public static AppLog instance() {
        if (singleton == null) {
            synchronized (AppLog.class) {
                if (singleton == null) {
                    singleton = new AppLog();
                }
            }
        }
        return singleton;
    }

    public static void setDebug(boolean buildConfigDebug, String loggableTag) {
        boolean isDebug = buildConfigDebug || Log.isLoggable(loggableTag, Log.DEBUG);
        if (isDebug) {
            setLogLevel(Log.DEBUG);
        } else {
            setLogLevel(Log.INFO);
        }
    }

    public static void setLogLevel(int level) {
        LOG_LEVEL = level;
    }

    public static void d(String msg) {
        log(Log.DEBUG, format(msg));
    }

    public static void d(final String msg, final Object... params) {
        log(Log.DEBUG, format(msg, params));
    }

    public static void v(String msg) {
        log(Log.VERBOSE, format(msg));
    }

    public static void e(String msg) {
        loge(format(msg), null);
    }

    public static void e(String msg, Throwable tr) {
        loge(format(msg), tr);
        instance().notifyExcpetion(tr);
    }

    public static void e(Throwable tr) {
        loge(tr.getMessage(), tr);
        instance().notifyExcpetion(tr);
    }

    public static void e(String msg, final Object... params) {
        loge(format(msg, params), null);
    }

    public static void w(String msg) {
        log(Log.VERBOSE, format(msg));
    }

    public static void v(String msg, final Object... params) {
        log(Log.VERBOSE, format(msg, params));
    }

    private static void log(int priority, String msg)
    {
        if (priority >= LOG_LEVEL)
        {
            LOGGER.println(priority, TAG, msg);
        }
    }

    private static void loge(String message,@Nullable Throwable tr) {
        String trace = LOGGER instanceof Logger.Android ? Log.getStackTraceString(tr) : "";
        LOGGER.println(Log.ERROR, TAG, message + '\n' + trace);
    }

    private static String format(final String msg, final Object... array) {
        String formatted;
        if (array == null || array.length == 0) {
            formatted = msg;
        } else {
            try {
                formatted = String.format(Locale.US, msg, array);
            } catch (IllegalFormatException ex) {
                e("IllegalFormatException: formatString='%s' numArgs=%d", msg, array.length);
                formatted = msg + " (An error occurred while formatting the message.)";
            }
        }
        final StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
        String string = "<unknown>";
        for (int i = 2; i < stackTrace.length; ++i) {
            final String className = stackTrace[i].getClassName();
            if (!className.equals(AppLog.class.getName())) {
                final String substring = className.substring(1 + className.lastIndexOf(46));
                string = substring.substring(1 + substring.lastIndexOf(36)) + "." + stackTrace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread().getId(), string, formatted);
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    private void notifyExcpetion(Throwable tr) {
        if (mListener != null) {
            mListener.onLogException(tr);
        }
    }

    public interface Listener {
        void onLogException(Throwable tr);
    }
}
