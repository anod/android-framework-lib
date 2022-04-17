package info.anodsplace.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PickGroup(options: Array<String>, selectedIndex: Int, modifier: Modifier = Modifier, onValueChanged: (index: Int) -> Unit = {}) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(selectedIndex) }
    FlowRow(
            modifier = modifier,
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 4.dp
    ) {
        options.forEachIndexed { index, text ->
            val selected = (index == selectedOption)
            Chip(
                    modifier = Modifier.height(32.dp),
                    shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 25)),
                    border = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant.copy(
                           alpha = if (selected) 1.0f else 0.3f
                    )),
                    colors = ChipDefaults.chipColors(
                            backgroundColor = if (selected) MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.secondary.copy(alpha = 0.1f),
                            contentColor = if (selected) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onSurface
                    ),
                    onClick = {
                        onValueChanged(index)
                        onOptionSelected(index)
                    }
            ) {
                Text(
                        text = text,
                        style = MaterialTheme.typography.body1.merge(),
                        modifier = Modifier
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 260, heightDp = 200)
@Composable
fun PickGroupPreview() {
    MaterialTheme {
        BackgroundSurface {
            PickGroup(
                    options = arrayOf("Banana", "Kiwi", "Apple", "Lemon"),
                    selectedIndex = 1,
                    onValueChanged = { }
            )
        }
    }
}