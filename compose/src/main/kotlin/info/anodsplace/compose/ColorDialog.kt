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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

fun Color.toColorHex(withAlpha: Boolean = true): String {
    var hexStr = "%08X".format(Locale.ROOT, (value shr 32).toLong())
    if (!withAlpha) {
        hexStr = hexStr.substring(2)
    }
    return hexStr
}

@Composable
fun ColorDialog(
        selected: Color?,
        showNone: Boolean = true,
        showAlpha: Boolean = true,
        title: String = "Pick a color",
        onColorSelected: (Color?) -> Unit = { }
) {
    val current = remember { mutableStateOf(selected) }

    AlertDialog(
            modifier = Modifier.padding(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title)
                    Spacer(modifier = Modifier.size(8.dp))
                    OutlinedTextField(
                            value = current.value?.toColorHex() ?: "",
                            singleLine = true,
                            onValueChange = {}
                    )
                }
            },
            text = {
                ColorDialogContent(current, showNone = showNone, showAlpha = showAlpha)
            },
            confirmButton = { },
//            buttons = {
//                ButtonsPanel(saveText = "Choose") {
//
//                }
//            },
            onDismissRequest = {
                onColorSelected(current.value)
            }
    )
}

@Composable
fun ColorDialogContent(color: MutableState<Color?>, showNone: Boolean, showAlpha: Boolean) {
    val currentNoAlpha = color.value?.copy(alpha = 1.0f)

    Column {
        ColorsTable(
                color,
                currentNoAlpha,
                showNone
        )
        if (showAlpha) {
            AlphaRow(color)
        }
    }
}

@Composable
fun AlphaRow(current: MutableState<Color?>) {
    val alphas = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)

    val currentAlpha = current.value?.alpha
    Row(
            modifier = Modifier.background(
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
            val modifier = Modifier.size(48.dp)
            val currentWithAlpha = current.value?.copy(alpha = alpha) ?: Color.Black
            val isSelected = alpha == currentAlpha
            Box(modifier = modifier) {
                ColorIcon(
                        modifier = modifier,
                        color = currentWithAlpha,
                        isSelected = isSelected,
                        onClick = { current.value = it })
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
fun ColorsTable(
        current: MutableState<Color?>,
        currentNoAlpha: Color?,
        showNone: Boolean,
) {
    val rows = (if (showNone)
        listOf(Color.Unspecified) + colors
    else colors).chunked(5)
    Column {
        for (row in rows) {
            Row {
                for (color in row) {
                    val modifier = Modifier.size(48.dp)

                    if (color == Color.Unspecified) {
                        ColorNone(
                                modifier = modifier,
                                isSelected = currentNoAlpha == null,
                                onClick = { current.value = null })
                    } else {
                        val isSelected = currentNoAlpha == color
                        Box(modifier = modifier) {
                            ColorIcon(
                                    modifier = modifier,
                                    color = color,
                                    isSelected = isSelected,
                                    onClick = { current.value = it })
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
fun ColorIcon(modifier: Modifier, color: Color, isSelected: Boolean, onClick: (Color) -> Unit) {
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
fun ColorNone(modifier: Modifier, isSelected: Boolean, onClick: () -> Unit) {
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
fun ColorDialogPreview() {
    val color: MutableState<Color?> = remember {
        mutableStateOf(Color(0xFF673AB7))
    }
    Surface {
        ColorDialogContent(color = color, showNone = true, showAlpha = true)
    }
}