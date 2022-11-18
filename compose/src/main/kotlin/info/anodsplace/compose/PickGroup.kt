package info.anodsplace.compose

import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickGroup(
    options: Array<String>,
    selectedIndex: Int, modifier:
    Modifier = Modifier,
    enabled: Boolean = true,
    border: SelectableChipBorder = FilterChipDefaults.filterChipBorder(
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        selectedBorderColor = MaterialTheme.colorScheme.primary,
        borderWidth = 1.dp,
        selectedBorderWidth = 1.dp
    ),
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors(),
    onValueChanged: (index: Int) -> Unit = {}
) {
    FlowRow(
            modifier = modifier,
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 4.dp
    ) {
        options.forEachIndexed { index, text ->
            val selected = (index == selectedIndex)
            FilterChip(
                    selected = selected,
                    enabled = enabled,
                    modifier = Modifier.height(32.dp),
                    border = border,
                    colors = colors,
                    onClick = {
                        onValueChanged(index)
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 260, heightDp = 200)
@Composable
fun PickGroupPreview() {
    MaterialTheme {
        Surface {
            PickGroup(
                    options = arrayOf("Banana", "Kiwi", "Apple", "Lemon"),
                    selectedIndex = 1,
                    onValueChanged = { }
            )
        }
    }
}