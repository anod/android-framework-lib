package info.anodsplace.ktx

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes

fun Context.resourceUri(@DrawableRes iconRes: Int): Uri {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .encodedAuthority(packageName)
        .appendEncodedPath(iconRes.toString())
        .build()
}

fun Context.resourceUri(type: String, name: String): Uri {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .encodedAuthority(packageName)
        .appendEncodedPath(type)
        .appendEncodedPath(name)
        .build()
}