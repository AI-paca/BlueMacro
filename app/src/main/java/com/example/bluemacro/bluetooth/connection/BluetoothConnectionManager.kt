package com.example.bluemacro.bluetooth.connection

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bluemacro.bluetooth.PermissionRequester
class BluetoothConnectionManager(private val context: Context, private val permissionRequester: PermissionRequester) {
    private var REQUEST_CODE_ENABLE_BLUETOOTH = 1
    private var REQUEST_CODE_MAKE_DISCOVERABLE = 2
    var bluetoothAdapter: BluetoothAdapter

    fun checkAndRequestBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissionRequester.requestBluetoothPermission()
        }
    }

    fun checkAndRequestBluetoothAdminPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissionRequester.requestBluetoothAdminPermission()
        }
    }

    fun checkAndRequestBluetoothConnectPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionRequester.requestBluetoothConnectPermission()
        }
    }

    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    fun isBluetoothAvailable(): Boolean {
        return bluetoothAdapter != null
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter.isEnabled
    }

    fun enableBluetooth(activity: Activity) {
        checkAndRequestBluetoothPermission()
        if (!bluetoothAdapter.isEnabled) {
            activity.startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_CODE_ENABLE_BLUETOOTH)
        }
    }

    fun disableBluetooth() {
        checkAndRequestBluetoothPermission()
        if (bluetoothAdapter.isEnabled) {
            bluetoothAdapter.disable()
        }
    }

    fun isDiscovering(): Boolean {
        checkAndRequestBluetoothAdminPermission()
        return bluetoothAdapter.isDiscovering
    }

    fun makeDiscoverable(activity: Activity) {
        checkAndRequestBluetoothAdminPermission()
        if (!bluetoothAdapter.isDiscovering) {
            activity.startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), REQUEST_CODE_MAKE_DISCOVERABLE)
        }
    }

    fun getPairedDevices(): Set<BluetoothDevice> {
        checkAndRequestBluetoothPermission()
        return bluetoothAdapter.bondedDevices
    }

    private val _connectedDevicesList = MutableLiveData<List<String>>()
    val connectedDevicesList: LiveData<List<String>> = _connectedDevicesList

    fun updateConnectedDevicesList() {
        _connectedDevicesList.value = getConnectedDevices()
    }

    fun unregisterBluetoothConnectionReceiver() {
        checkAndRequestBluetoothPermission()
        checkAndRequestBluetoothAdminPermission()

        context.unregisterReceiver(bluetoothConnectionReceiver)
    }


    fun getConnectedDevices(): List<String> {
        val connectedDevices = mutableListOf<String>()
        checkAndRequestBluetoothPermission()
        val pairedDevices = bluetoothAdapter.bondedDevices
        for (device in pairedDevices) {
            val method = device.javaClass.getMethod("isConnected")
            val connected = method.invoke(device) as Boolean

            if (connected) {
                connectedDevices.add(device.name)
            }
        }

        return connectedDevices
    }

    private val bluetoothConnectionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (BluetoothDevice.ACTION_ACL_CONNECTED == intent.action || BluetoothDevice.ACTION_ACL_DISCONNECTED == intent.action) {
                updateConnectedDevicesList()
            }
        }
    }

    fun registerBluetoothConnectionReceiver() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        context.registerReceiver(bluetoothConnectionReceiver, filter)
    }

}



