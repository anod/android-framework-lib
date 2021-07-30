package info.anodsplace.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

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
    var hexStr = String.format("%08X", value.toInt())
    if (!withAlpha) {
        hexStr = hexStr.substring(2)
    }
    return hexStr
}

@Composable
fun ColorDialog(selected: Color?, onColorSelected: (Color?) -> Unit) {
    var current = remember { mutableStateOf(selected) }
    val currentNoAlpha = current.value?.copy(alpha = 1.0f)

    AlertDialog(
            modifier = Modifier.padding(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Pick a color")
                    Spacer(modifier = Modifier.size(8.dp))
                    OutlinedTextField(
                            value = current.value?.toColorHex() ?: "",
                            singleLine = true,
                            onValueChange = {}
                    )
                }
            },
            text = {
                Column {
                    ColorsTable(current, currentNoAlpha)
                    AlfaRow(current)
                }
            },
            buttons = {
                ButtonsPanel(saveText = "Choose") {

                }
            },
            onDismissRequest = {
                onColorSelected(current.value)
            }
    )
}

@Composable
fun AlfaRow(current: MutableState<Color?>) {

}

@Composable
fun ColorsTable(current: MutableState<Color?>, currentNoAlpha: Color?) {
    val rows = colors.chunked(5)
    Column {
        for (row in rows) {
            Row {
                for (color in row) {
                    val isSelected = currentNoAlpha == color
                    ColorIcon(color, isSelected = isSelected, onClick = { current.value = it })
                }
            }
        }
    }
}

@Composable
fun ColorIcon(color: Color, isSelected: Boolean, onClick: (Color) -> Unit) {
    Icon(
            modifier = Modifier
                    .size(48.dp)
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
