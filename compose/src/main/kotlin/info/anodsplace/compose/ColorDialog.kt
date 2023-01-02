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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import java.lang.Exception
import java.util.*

private val colorsAll = listOf(
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
)

private val colorsWithNone = listOf(
    Color.Unspecified,
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
    Color(0xFFFF9800),
    Color(0xFFFF5722),
    Color(0xFF795548),
    Color(0xFF9E9E9E),
    Color(0xFF607D8B),
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

private val size = 40.dp

@Composable
fun ColorDialogContent(
    color: Color?,
    modifier: Modifier = Modifier,
    showNone: Boolean = true,
    showAlpha: Boolean = true,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    surfaceContent: Color = MaterialTheme.colorScheme.onSurface,
    onColorChange: (Color?) -> Unit = { }
) {
    val currentNoAlpha = color?.copy(alpha = 1.0f)

    Surface(
        modifier = modifier,
        color = surfaceColor,
        contentColor = surfaceContent
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ColorsTable(
                currentNoAlpha = currentNoAlpha,
                showNone = showNone,
                onColorChange = onColorChange
            )
            Divider(modifier = Modifier
                .padding(top = 2.dp, bottom = 2.dp)
                .width(240.dp)
            )
            LightRow(
                color = color ?: Color.Black,
                onColorChange = onColorChange
            )
            DarkRow(
                color = color ?: Color.White,
                onColorChange = onColorChange
            )
            if (showAlpha) {
                AlphaRow(color, onColorChange = onColorChange)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorInput(color: Color?, onColorChange: (Color?) -> Unit = { }) {
    OutlinedTextField(
        modifier = Modifier.width(140.dp),
        value = color?.toColorHex() ?: "",
        singleLine = true,
        onValueChange = {
            try {
                if (it.length >= 6) {
                    val parsed = it.toColorInt()
                    onColorChange(Color(parsed))
                }
            } catch (_: Exception) { }
        },
        textStyle = MaterialTheme.typography.labelMedium
    )
}

@Composable
private fun LightRow(color: Color, onColorChange: (Color?) -> Unit, modifier: Modifier = Modifier) {
    val colors = remember(color) {
        listOf(0.15f, 0.30f, 0.45f, 0.6f, 0.75f, 1.0f).map { interpolation ->
           lerp(color, Color.White, interpolation)
        }
    }
    ColorsRow(
        colors = colors,
        selected = color,
        onColorChange = onColorChange
    )
}

@Composable
private fun DarkRow(color: Color, onColorChange: (Color?) -> Unit, modifier: Modifier = Modifier) {
    val colors = remember(color) {
        listOf(0.15f, 0.30f, 0.45f, 0.6f, 0.75f, 1.0f).map { interpolation ->
            lerp(color, Color.Black, interpolation)
        }
    }
    ColorsRow(
        colors = colors,
        selected = color,
        onColorChange = onColorChange
    )
}

@Composable
private fun AlphaRow(color: Color?, onColorChange: (Color?) -> Unit, modifier: Modifier = Modifier) {
    val colors = remember(color) {
        listOf(0.15f, 0.30f, 0.45f, 0.6f, 0.75f, 1.0f).map { alpha ->
            color?.copy(alpha = alpha) ?: Color.Black
        }
    }

    ColorsRow(
        colors = colors,
        selected = color ?: Color.Black,
        onColorChange = onColorChange,
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                0.0f to Color.White,
                1.0f to Color.Gray,
                startY = 0.0f,
                endY = 100.0f
            ),
            shape = RoundedCornerShape(8.dp)
        )
    )
}

@Composable
private fun ColorsRow(colors: List<Color>, selected: Color, onColorChange: (Color) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
    ) {
        for (color in colors) {
            val tint by remember(key1 = color) {
                derivedStateOf {
                    if (color.luminance() > 0.3) Color.Black else Color.White
                }
            }
            val isSelected = color == selected
            Box(modifier = Modifier.size(size)) {
                ColorIcon(
                    modifier = Modifier.size(size),
                    color = color,
                    isSelected = isSelected,
                    onClick = { onColorChange(it) })
                if (isSelected) {
                    Icon(
                        modifier = Modifier
                            .size(size)
                            .padding(8.dp)
                            .alpha(0.8f),
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = tint
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
    val rows = (if (showNone) colorsWithNone else colorsAll)
        .chunked(6)
    Column(
        modifier = modifier
    ) {
        for (row in rows) {
            Row {
                for (color in row) {
                    val colorModifier = Modifier.size(size)

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
                                            .size(size)
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
                .padding(2.dp)
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
                .padding(2.dp)
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
private fun ColorDialogWithNonePreview() {
    var color: Color? by remember { mutableStateOf(Color(0xFF673AB7)) }
    ColorDialogContent(color = color, onColorChange = { color = it })
}

@Preview
@Composable
private fun ColorDialogPreview() {
    var color: Color? by remember { mutableStateOf(Color(0xFF673AB7)) }
    ColorDialogContent(color = color, onColorChange = { color = it }, showNone = false)
}