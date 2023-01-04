package info.anodsplace.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pageview
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class CheckBoxItem(
    val key: String,
    val checked : Boolean,
    val title: String? = null,
    val icon: ImageVector? = null,
    val iconTint: Color? = null
)

@Composable
fun CheckBoxList(items: List<CheckBoxItem>, modifier: Modifier = Modifier, onCheckedChange: (CheckBoxItem) -> Unit) {
    rememberScrollState(0)
    LazyColumn(modifier = modifier) {
        items(items) { item ->
            val isItemChecked = item.checked
            Row(modifier = Modifier
                    .toggleable(value = isItemChecked, onValueChange = { newState ->
                        onCheckedChange(item.copy(checked = newState))
                    })
            ) {
                Checkbox(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        checked = isItemChecked,
                        onCheckedChange = { newState ->
                            onCheckedChange(item.copy(checked = newState))
                        },
                )
                Text(
                        text = item.title ?: item.key,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .weight(1.0f)
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                            .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (item.icon != null) {
                    if (item.iconTint == null) {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            imageVector = item.icon,
                            contentDescription = item.title ?: item.key,
                        )
                    } else {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            imageVector = item.icon,
                            contentDescription = item.title ?: item.key,
                            tint = item.iconTint
                        )
                    }
                }
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
    items: List<CheckBoxItem>,
    onCheckedChange: (CheckBoxItem) -> Unit,
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
            items = listOf(
                CheckBoxItem(
                    key = "ACTIVITY_NEW_TASK", checked = true
                ),
                CheckBoxItem(
                    key = "ACTIVITY_NEW_DOCUMENT", title = "CustomTitle", checked = false, icon = Icons.Default.Pageview
                ),
            ),
            onDismissRequest = {},
            onCheckedChange = {}
        )
    }
}