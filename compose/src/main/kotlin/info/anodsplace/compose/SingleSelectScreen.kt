package info.anodsplace.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview

class SingleScreenState(
        val title: String,
        val items: Map<String, String>,
)

@Composable
fun SingleScreenList(listContent: Map<String, String>, modifier: Modifier = Modifier, onSelect: (key: String, value: String) -> Unit) {
    rememberScrollState(0)
    LazyColumn(modifier = modifier) {
        items(listContent.entries.toList()) { item ->
            Row(modifier = Modifier
                            .clickable(onClick = { onSelect(item.key, item.value) })
            ) {
                Text(
                        text = item.key,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 8.dp)
                                .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun SingleListScreen(state: SingleScreenState, onSelect: (key: String, value: String) -> Unit) {
    Surface(
            modifier = Modifier.padding(16.dp),
            elevation = 2.dp,
            color = MaterialTheme.colors.surface) {
        Column {
            Box(modifier = Modifier.padding(16.dp)) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(text = state.title, style = MaterialTheme.typography.subtitle1)
                }
            }
            SingleScreenList(
                    listContent = state.items,
                    modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                    onSelect = onSelect
            )
        }
    }
}

@Preview
@Composable
fun SingleListScreenPreview() {
    val state = SingleScreenState("Action", emptyMap())
    MaterialTheme {
        Box(contentAlignment = Alignment.Center) {
            SingleListScreen(state, onSelect = { _, _ ->  })
        }
    }
}