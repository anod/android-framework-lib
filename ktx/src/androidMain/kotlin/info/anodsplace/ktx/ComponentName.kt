package info.anodsplace.ktx

import android.content.ComponentName
import android.net.Uri

const val SCHEME_APPLICATION_ICON = "application.icon"

val ComponentName.appIconUri: Uri
    get() = Uri.fromParts(
        SCHEME_APPLICATION_ICON,
        flattenToShortString(),
        null
    )