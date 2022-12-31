package info.anodsplace.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.*

private val colors = listOf(
        Color(0xFFF44336),
        Color(0xFFE91E63),
        Color(0xFF9C27B0),
        Color(0xFF673AB7),
        Color(0xFF3F51B5),
        Color(0xFF2196F3),
        Color(0xFF03A9F4),
        Color(0xFF00BCD4),
        Color(0xFF009688),
        Color(0xFF8BC34A),
        Color(0xFFCDDC39),
        Color(0xFFFFEB3B),
        Color(0xFFFFC107),
        Color(0xFFFF9800),
        Color(0xFFFF5722),
        Color(0xFF795548),
        Color(0xFF9E9E9E),
        Color(0xFF607D8B),
        Color(0xFFFFFFFF),
        Color(0xFF000000),
)

val Color.isVisible: Boolean
    get() = alpha > 0.00001

val Color.isNotVisible: Boolean
    get() = !isVisible

val Color?.isNotVisible: Boolean
    get() = this?.isNotVisible ?: true

fun Color.toColorHex(withAlpha: Boolean = true): String {
    var hexStr = "%08X".format(Locale.ROOT, (value shr 32).toLong())
    if (!withAlpha) {
        hexStr = hexStr.substring(2)
    }
    return hexStr
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorDialogContent(
    color: Color?,
    showNone: Boolean = true,
    showAlpha: Boolean = true,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    surfaceContent: Color = MaterialTheme.colorScheme.onSurface,
    onColorChange: (Color?) -> Unit = { }
) {
    val currentNoAlpha = color?.copy(alpha = 1.0f)

    Surface(
        color = surfaceColor,
        contentColor = surfaceContent
    ) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier.width(140.dp),
                value = color?.toColorHex() ?: "",
                singleLine = true,
                onValueChange = {},
                textStyle = MaterialTheme.typography.labelMedium
            )

            ColorsTable(
                modifier = Modifier.padding(top = 16.dp),
                currentNoAlpha = currentNoAlpha,
                showNone = showNone,
                onColorChange = onColorChange
            )
            if (showAlpha) {
                AlphaRow(color, onColorChange = onColorChange)
            }
        }
    }
}

@Composable
private fun AlphaRow(color: Color?, onColorChange: (Color?) -> Unit, modifier: Modifier = Modifier) {
    val alphas = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)

    val currentAlpha = color?.alpha
    Row(
            modifier = modifier.background(
                    brush = Brush.verticalGradient(
                            0.0f to Color.White,
                            1.0f to Color.Gray,
                            startY = 0.0f,
                            endY = 100.0f
                    ),
                    shape = RoundedCornerShape(8.dp)
            )
    ) {
        for (alpha in alphas) {
            val colorModifier = Modifier.size(48.dp)
            val currentWithAlpha = color?.copy(alpha = alpha) ?: Color.Black
            val isSelected = alpha == currentAlpha
            Box(modifier = colorModifier) {
                ColorIcon(
                        modifier = colorModifier,
                        color = currentWithAlpha,
                        isSelected = isSelected,
                        onClick = { onColorChange(it) })
                if (isSelected) {
                    Icon(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(8.dp)
                                .alpha(0.8f),
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = if (currentWithAlpha.luminance() > 0.3)
                                Color.Black else Color.White
                    )
                }
            }
        }
    }

}

@Composable
private fun ColorsTable(
    currentNoAlpha: Color?,
    showNone: Boolean,
    onColorChange: (Color?) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = (if (showNone) listOf(Color.Unspecified) + colors else colors)
        .chunked(5)
    Column(
        modifier = modifier
    ) {
        for (row in rows) {
            Row {
                for (color in row) {
                    val colorModifier = Modifier.size(48.dp)

                    if (color == Color.Unspecified) {
                        ColorNone(
                                modifier = colorModifier,
                                isSelected = currentNoAlpha == null,
                                onClick = { onColorChange(null) })
                    } else {
                        val isSelected = currentNoAlpha == color
                        Box(modifier = colorModifier) {
                            ColorIcon(
                                    modifier = colorModifier,
                                    color = color,
                                    isSelected = isSelected,
                                    onClick = { onColorChange(it) })
                            if (isSelected) {
                                Icon(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .padding(8.dp)
                                            .alpha(0.8f),
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = if (color.luminance() > 0.3)
                                            Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorIcon(modifier: Modifier, color: Color, isSelected: Boolean, onClick: (Color) -> Unit) {
    Icon(
            modifier = modifier
                .padding(4.dp)
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = Color.Gray,
                    shape = CircleShape
                )
                .clip(shape = CircleShape)
                .clickable { onClick(color) },
            painter = ColorPainter(color),
            tint = Color.Unspecified,
            contentDescription = color.toString()
    )
}

@Composable
private fun ColorNone(modifier: Modifier, isSelected: Boolean, onClick: () -> Unit) {
    Icon(
            modifier = modifier
                .padding(4.dp)
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = Color.Gray,
                    shape = CircleShape
                )
                .clip(shape = CircleShape)
                .clickable { onClick() },
            imageVector = Icons.Outlined.Cancel,
            tint = Color.Unspecified,
            contentDescription = "None"
    )
}

@Preview
@Composable
private fun ColorDialogPreview() {
    var color: Color? by remember { mutableStateOf(Color(0xFF673AB7)) }
    ColorDialogContent(color = color, onColorChange = { color = it })
}