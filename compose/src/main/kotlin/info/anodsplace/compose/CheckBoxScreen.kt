package info.anodsplace.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CheckBoxList(items: Map<String, Boolean>, modifier: Modifier = Modifier, onCheckedChange: (key: String, checked: Boolean) -> Unit) {
    rememberScrollState(0)
    LazyColumn(modifier = modifier) {
        items(items.entries.toList()) { item ->
            val isItemChecked = item.value
            Row(modifier = Modifier
                    .toggleable(value = isItemChecked, onValueChange = { newState ->
                        onCheckedChange(item.key, newState)
                    })
            ) {
                Checkbox(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        checked = isItemChecked,
                        onCheckedChange = { newState ->
                            onCheckedChange(item.key, newState)
                        },
                )
                Text(
                        text = item.key,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 8.dp)
                                .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CheckBoxScreen(
    titleText: String,
    saveText: String,
    items: Map<String, Boolean>,
    onCheckedChange: (key: String, checked: Boolean) -> Unit,
    onDismissRequest: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Box(modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Start)) {
                Text(titleText, style = MaterialTheme.typography.titleMedium)
            }
            CheckBoxList(
                items = items,
                modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(0.1f, fill = true),
                onCheckedChange = onCheckedChange
            )
            ButtonsPanel(actionText = saveText, onDismissRequest = onDismissRequest, onAction = onDismissRequest)
        }
    }
}

@Preview
@Composable
fun CheckBoxScreenPreview() {
    MaterialTheme {
        CheckBoxScreen(
            titleText = "Categories",
            saveText = "Save",
            items = mapOf("ACTIVITY_NEW_TASK" to false, "ACTIVITY_NEW_DOCUMENT" to true),
            onDismissRequest = {},
            onCheckedChange = { _, _ -> }
        )
    }
}