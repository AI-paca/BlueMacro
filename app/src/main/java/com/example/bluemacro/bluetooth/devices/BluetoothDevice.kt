package com.example.bluemacro.bluetooth.devices

import android.content.Context
import com.example.bluemacro.bluetooth.PermissionRequester
import com.example.bluemacro.bluetooth.connection.BluetoothConnectionManager

class BluetoothDevice (context: Context, permissionRequester: PermissionRequester) {
    private val bluetoothConnectionManager = BluetoothConnectionManager.getInstance(context, permissionRequester)
}