package info.anodsplace.compose

import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalMaterial3Api::class)
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
            FilterChip(
                    selected = selected,
                    modifier = Modifier.height(32.dp),
                    // shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 25)),
                    border = FilterChipDefaults.filterChipBorder(
                            borderColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                            selectedBorderColor = MaterialTheme.colorScheme.tertiary,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.dp
                    ),
                    colors = FilterChipDefaults.filterChipColors(
//                            labelColor =
//                            backgroundColor = if (selected) MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.secondary.copy(alpha = 0.1f),
//                            contentColor = if (selected) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onSurface
                    ),
                    onClick = {
                        onValueChanged(index)
                        onOptionSelected(index)
                    },
                    label = {
                        Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge.merge(),
                                modifier = Modifier
                        )
                    }
            )
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