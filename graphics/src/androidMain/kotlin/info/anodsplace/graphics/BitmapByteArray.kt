package info.anodsplace.graphics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

fun ByteArray?.toBitmap(opts: BitmapFactory.Options): Bitmap? {
    val data = this ?: return null
    if (data.isEmpty()) {
        return null
    }
    return BitmapFactory.decodeByteArray(data, 0, data.size, opts)
}

fun Bitmap.toByteArray(): ByteArray? {
    // Try go guesstimate how much space the icon will take when serialized
    // to avoid unnecessary allocations/copies during the write.
    val size = width * height * 4
    val out = ByteArrayOutputStream(size)

    compress(Bitmap.CompressFormat.PNG, 100, out)
    out.flush()
    out.close()
    return out.toByteArray()
}