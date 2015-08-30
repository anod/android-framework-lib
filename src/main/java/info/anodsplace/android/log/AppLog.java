package info.anodsplace.android.log;

import android.util.Log;


import java.util.IllegalFormatException;
import java.util.Locale;


/**
 * @author alex
 * @date 2015-05-11
 */
public class AppLog {

    private static final String TAG = "AppLog";
    public static boolean DEBUG;
    static volatile AppLog singleton = null;

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
        DEBUG = buildConfigDebug ? buildConfigDebug : Log.isLoggable(loggableTag, Log.DEBUG);
    }

    public static void d(String msg) {
        if (DEBUG) Log.d(TAG, format(msg));
    }

    public static void d(final String msg, final Object... params) {
        if (DEBUG) Log.d(TAG, format(msg, params));
    }

    public static void v(String msg) {
        Log.v(TAG, format(msg));
    }

    public static void e(String msg) {
        Log.e(TAG, format(msg));
    }

    public static void e(String msg, Throwable tr) {
        Log.e(TAG, format(msg), tr);
        instance().notifyExcpetion(tr);
    }

    public static void e(Throwable tr) {
        Log.e(TAG, tr.getMessage(), tr);
        instance().notifyExcpetion(tr);
    }

    /*
    public static void e(RetrofitError error) {
        e("RetrofitError: " + error.getClass().getSimpleName() + ": " + error.getMessage(), error.getCause());
        instance().notifyExcpetion(error.getCause());
    }
    */

    public static void e(String msg, final Object... params) {
        Log.e(TAG, format(msg, params));
    }

    public static void w(String msg) {
        Log.v(TAG, format(msg));
    }

    public static void v(String msg, final Object... params) {
        Log.v(TAG, format(msg, params));
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
