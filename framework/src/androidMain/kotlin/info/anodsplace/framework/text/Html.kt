package info.anodsplace.framework.text

import android.text.SpannableString
import android.text.Spanned
import info.anodsplace.applog.AppLog

/**
 * @author Alex Gavrishev
 * @date 14/09/2017
 */
object Html {

    fun parse(source: String): Spanned {
        try {
            if (source.isBlank()) {
                return SpannableString(source)
            }
            return android.text.Html.fromHtml(source, android.text.Html.FROM_HTML_MODE_COMPACT)
        } catch (e: RuntimeException) {
            AppLog.e(e)
            return SpannableString(source)
        } catch (e: android.util.AndroidRuntimeException) {
            //
            // Fatal Exception: android.util.AndroidRuntimeException
            //        android.webkit.WebViewFactory$MissingWebViewPackageException: Failed to load WebView provider: No WebView installed

            AppLog.e(e)
            return SpannableString(source)
        } catch (e: Exception) {
            //
            // Fatal Exception: android.util.AndroidRuntimeException
            //        android.webkit.WebViewFactory$MissingWebViewPackageException: Failed to load WebView provider: No WebView installed

            AppLog.e(e)
            return SpannableString(source)
        }
    }

}