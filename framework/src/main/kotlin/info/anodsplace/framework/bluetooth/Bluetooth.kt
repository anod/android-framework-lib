package info.anodsplace.framework.bluetooth

import android.bluetooth.BluetoothAdapter

object Bluetooth {

    val state: Int
        get() {
            val btAdapter = BluetoothAdapter.getDefaultAdapter()
                    ?: return BluetoothAdapter.STATE_OFF
            return btAdapter.state
        }

    fun switchOn() {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        btAdapter?.enable()
    }

    fun switchOff() {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        btAdapter?.disable()
    }
}
