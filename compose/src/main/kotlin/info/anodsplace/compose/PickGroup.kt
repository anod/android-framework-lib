package info.anodsplace.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PickGroup(
    options: Array<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    border: BorderStroke = FilterChipDefaults.filterChipBorder(
        enabled = enabled,
        selected = selectedIndex >= 0,
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
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement  = Arrangement.spacedBy(4.dp),
    ) {
        options.forEachIndexed { index, text ->
            val selected = (index == selectedIndex)
            FilterChip(
                selected = selected,
                enabled = enabled,
                modifier = Modifier
                    .height(32.dp),
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