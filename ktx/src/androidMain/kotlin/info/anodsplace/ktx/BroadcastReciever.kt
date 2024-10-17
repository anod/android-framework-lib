package info.anodsplace.ktx

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun Context.broadcastReceiver(filter: IntentFilter): Flow<Intent?> = callbackFlow {
    val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            trySendBlocking(intent)
                .onFailure { _ -> }
        }
    }

    registerReceiver(bluetoothReceiver, filter)

    awaitClose { unregisterReceiver(bluetoothReceiver) }
}