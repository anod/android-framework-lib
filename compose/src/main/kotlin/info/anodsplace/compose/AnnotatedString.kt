package info.anodsplace.compose

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.text.util.Linkify
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

@OptIn(ExperimentalTextApi::class)
fun Spanned.toAnnotatedString(linkColor: Color): AnnotatedString = buildAnnotatedString {
    append(this@toAnnotatedString.toString())
    val spannable = SpannableString(this@toAnnotatedString)
    Linkify.addLinks(spannable, Linkify.WEB_URLS)
    spannable.getSpans(0, spannable.length, Any::class.java).forEach { span ->
        val start = spannable.getSpanStart(span)
        val end = spannable.getSpanEnd(span)
        when (span) {
            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                Typeface.BOLD_ITALIC -> addStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic), start, end)
            }
            is UnderlineSpan -> addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
            is ForegroundColorSpan -> addStyle(SpanStyle(color = Color(span.foregroundColor)), start, end)
            is URLSpan -> {
                addStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = linkColor), start, end)
                addUrlAnnotation(UrlAnnotation(span.url), start, end)
                addStringAnnotation(tag = "URL", annotation = span.url, start, end)
            }
            is ImageSpan -> {
                addStringAnnotation("androidx.compose.foundation.text.inlineContent", span.source ?: "image", start, end)
            }
        }
    }
}