package info.anodsplace.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ButtonsPanel(saveText: String, onDismissRequest: () -> Unit) {
    Column {
        Divider()
        Row(
                Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Button(onClick = onDismissRequest, modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Button(onClick = onDismissRequest, modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(text = saveText)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}