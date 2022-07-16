package info.anodsplace.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class CheckBoxScreenState(
        val title: String,
        val items: Map<String, Any?>,
        val checked: SnapshotStateList<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckBoxList(items: Map<String, Any?>, checked: SnapshotStateList<String>, modifier: Modifier = Modifier, onCheckedChange: (key: String, value: Any?, checked: Boolean) -> Unit) {
    val checkedMap = checked.associateWith { true }
    rememberScrollState(0)
    LazyColumn(modifier = modifier) {
        items(items.entries.toList()) { item ->
            val isItemChecked = checkedMap.containsKey(item.key)
            val (itemChecked, onItemChecked) = remember { mutableStateOf(isItemChecked) }
            Row(modifier = Modifier
                    .toggleable(value = itemChecked, onValueChange = onItemChecked)
            ) {
                Checkbox(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        checked = itemChecked,
                        onCheckedChange = { newState ->
                            if (checkedMap.containsKey(item.key)) {
                                if (!newState) {
                                    checked.remove(item.key)
                                }
                            } else {
                                if (newState) {
                                    checked.add(item.key)
                                }
                            }
                            onItemChecked(newState)
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
fun CheckBoxScreen(saveText: String, state: CheckBoxScreenState, onDismissRequest: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Box(modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Start)) {
                // CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(state.title, style = MaterialTheme.typography.titleMedium)
                // }
            }
            CheckBoxList(
                    items = state.items,
                    checked = state.checked,
                    modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(0.1f, fill = true),
            ) { _: String, _: Any?, _: Boolean -> }
            ButtonsPanel(saveText = saveText, onDismissRequest = onDismissRequest)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun CheckBoxScreenPreview() {
    val state = CheckBoxScreenState(
            "Categories", emptyMap(), mutableStateListOf("ACTIVITY_NEW_TASK", "ACTIVITY_NEW_DOCUMENT")
    )
    MaterialTheme {
        Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(title = {
                        Text(text = "Preview")
                    })
                },
                containerColor = MaterialTheme.colorScheme.background,
                content = {
                    Box(modifier = Modifier.padding(it),
                            contentAlignment = Alignment.Center) {
                        CheckBoxScreen(saveText = "Save", state, onDismissRequest = {})
                    }
                }
        )
    }
}