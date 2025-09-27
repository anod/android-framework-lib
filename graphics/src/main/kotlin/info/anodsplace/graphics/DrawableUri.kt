package info.anodsplace.graphics

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * @date 14/04/2017.
 */
class DrawableUri(private val context: Context) {

    class OpenResourceIdResult(val r: Resources, val id: Int)

    data class ResolveProperties(
            val maxIconSize: Int,
            val targetDensity: Int,
            val densityDpi: Int
    ) {
        constructor(
            maxIconSize: Int,
            targetDensity: Int,
            context: Context
        ) : this(
            maxIconSize = maxIconSize,
            targetDensity = targetDensity,
            densityDpi = context.resources.displayMetrics.densityDpi
        )
    }
    /**
     * Source android.widget.ImageView
     */
    fun resolve(uri: Uri, properties: ResolveProperties): Drawable? {
        var d: Drawable? = null
        val scheme = uri.scheme
        if (ContentResolver.SCHEME_ANDROID_RESOURCE == scheme) {
            d = getDrawableByUri(uri, properties.targetDensity)
        } else if (ContentResolver.SCHEME_CONTENT == scheme || ContentResolver.SCHEME_FILE == scheme) {
            try {
                val bmp = decodeSampledBitmapFromStream(uri, properties.maxIconSize, properties.maxIconSize)
                bmp.density = properties.densityDpi
                d = bmp.toDrawable(context.resources)
            } catch (e: Exception) {
                Log.w("ShortcutEditActivity", "Unable to open content: $uri", e)
            }

        } else {
            d = Drawable.createFromPath(uri.toString())
        }

        return d
    }

    private fun getDrawableByUri(uri: Uri, targetDensity: Int): Drawable? {
        var d: Drawable? = null
        try {
            // Load drawable through Resources, to get the source density information
            val r = getResourceId(uri)
            d = ResourcesCompat.getDrawableForDensity(r.r, r.id, targetDensity, null)
        } catch (e: Exception) {
            Log.w("ShortcutEditActivity", "Unable to open content: $uri", e)
        }

        return d
    }

    @Throws(FileNotFoundException::class)
    fun decodeSampledBitmapFromStream(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        var inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream, null, options)
        closeStream(inputStream)
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        inputStream = context.contentResolver.openInputStream(uri)
        val bmp = BitmapFactory.decodeStream(inputStream, null, options)
        closeStream(inputStream)

        return bmp!!
    }

    /**
     * From android.content.ContentResolver
     */
    @Throws(FileNotFoundException::class)
    fun getResourceId(uri: Uri): OpenResourceIdResult {
        val authority = uri.authority
        val r: Resources
        if (authority.isNullOrEmpty()) {
            throw FileNotFoundException("No authority: $uri")
        } else {
            try {
                r = context.packageManager.getResourcesForApplication(authority)
            } catch (_: PackageManager.NameNotFoundException) {
                throw FileNotFoundException("No package found for authority: $uri")
            }

        }
        val path = uri.pathSegments ?: throw FileNotFoundException("No path: $uri")
        val id = when (path.size) {
            1 -> {
                try {
                    Integer.parseInt(path[0])
                } catch (_: NumberFormatException) {
                    throw FileNotFoundException("Single path segment is not a resource ID: $uri")
                }

            }
            2 -> {
                r.getIdentifier(path[1], path[0], authority)
            }
            else -> {
                throw FileNotFoundException("More than two path segments: $uri")
            }
        }
        if (id == 0) {
            throw FileNotFoundException("No resource found for: $uri")
        }
        return OpenResourceIdResult(r, id)
    }

    companion object {

        internal fun closeStream(`is`: InputStream?) {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (_: IOException) {
                }

            }
        }

        fun calculateInSampleSize(
                options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }
    }

}
