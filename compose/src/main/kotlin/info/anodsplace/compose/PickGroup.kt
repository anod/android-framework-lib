package info.anodsplace.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
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
            modifier = modifier.selectableGroup()
    ) {
        options.forEachIndexed { index, text ->
            val selected = (index == selectedOption)
            Chip(
                    modifier = Modifier.padding(start = if (index > 0) 8.dp else 0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(
                           alpha = if (selected) 1.0f else 0.3f
                    )),
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


@Preview(showBackground = true)
@Composable
fun WithGreenBackground() {
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