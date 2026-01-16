package info.anodsplace.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class SingleSelectListState(
    val items: Map<String, String>,
)

@Composable
fun SingleSelectLazyList(state: SingleSelectListState, modifier: Modifier = Modifier, onSelect: (key: String, value: String) -> Unit) {
    rememberScrollState(0)
    LazyColumn(modifier = modifier) {
        items(state.items.entries.toList()) { item ->
            Row(modifier = Modifier
                    .clickable(onClick = { onSelect(item.key, item.value) })
            ) {
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
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Preview
@Composable
fun SingleListScreenPreview() {
    val state = SingleSelectListState( mapOf("Banana" to "Kiwi", "Apple" to "Orange", "Peach" to "Pear"))
    MaterialTheme {
        Surface {
            SingleSelectLazyList(state, onSelect = { _, _ -> })
        }
    }
}