package info.anodsplace.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BottomSheet(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    val transitionState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    Dialog(
        onDismissRequest = {
            transitionState.targetState = false
            onDismissRequest()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visibleState = transitionState,
            enter = slideInVertically(
                initialOffsetY = { it },
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                content()
            }
        }
    }
}