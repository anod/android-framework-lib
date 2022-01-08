package info.anodsplace.compose

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.squareup.picasso.Picasso
import info.anodsplace.applog.AppLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val LocalPicasso = compositionLocalOf<Picasso> {
    error("No Picasso provided")
}

sealed class PicassoImage {
    object Loading : PicassoImage()
    class Loaded(val image: ImageBitmap) : PicassoImage()
    object Error : PicassoImage()
}

@Composable
fun PicassoIcon(uri: Uri, modifier: Modifier = Modifier, contentDescription: String? = null) {
    val imageResult by loadPicassoImage(uri)
    when (imageResult) {
        is PicassoImage.Loading -> {
            Box(modifier = modifier) {

            }
        }
        is PicassoImage.Loaded -> {
            Icon(
                modifier = modifier,
                bitmap = (imageResult as PicassoImage.Loaded).image,
                contentDescription = contentDescription,
                tint = Color.Unspecified
            )
        }
        is PicassoImage.Error -> {
            Icon(
                modifier = modifier,
                imageVector = Icons.Filled.Cancel,
                contentDescription = contentDescription,
                tint = Color.White
            )
        }
    }
}

@Composable
fun loadPicassoImage(
    url: Uri,
    picasso: Picasso = LocalPicasso.current
): State<PicassoImage> {
    return produceState<PicassoImage>(initialValue = PicassoImage.Loading, url, picasso) {
        value = try {
            val result: Bitmap? = withContext(Dispatchers.IO) { picasso.load(url).get() }
            if (result == null) {
                PicassoImage.Error
            } else {
                PicassoImage.Loaded(result.asImageBitmap())
            }
        } catch (e: Exception) {
            AppLog.e("Failed to load $url", e)
            PicassoImage.Error
        }
    }
}