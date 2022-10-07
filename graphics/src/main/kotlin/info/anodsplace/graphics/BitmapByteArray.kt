package info.anodsplace.graphics

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun ByteArray?.toBitmap(opts: BitmapFactory.Options): Bitmap? {
    val data = this ?: return null
    if (data.isEmpty()) {
        return null
    }
    return BitmapFactory.decodeByteArray(data, 0, data.size, opts)
}