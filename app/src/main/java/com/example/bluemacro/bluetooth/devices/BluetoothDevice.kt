package com.example.bluemacro.bluetooth.devices

import android.content.Context
import com.example.bluemacro.bluetooth.PermissionRequester
import com.example.bluemacro.bluetooth.connection.BluetoothConnectionManager
import com.example.bluemacro.bluetooth.events.BluetoothEvent

class BluetoothDevice(private val bluetoothEvent: BluetoothEvent) {
    fun onButtonPress() {
        bluetoothEvent.onButtonPress()
    }
}

