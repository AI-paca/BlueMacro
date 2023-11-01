package com.example.bluemacro.model

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MyBluetoothManager(private val activity: Activity) {
    private var REQUEST_CODE_ENABLE_BLUETOOTH = 1
    private var REQUEST_CODE_MAKE_DISCOVERABLE = 2
    lateinit var bluetoothAdapter: BluetoothAdapter

    private val REQUEST_CODE_BLUETOOTH_PERMISSION = 100

    fun checkAndRequestBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH), REQUEST_CODE_BLUETOOTH_PERMISSION)
        }
    }

    private val REQUEST_CODE_BLUETOOTH_ADMIN_PERMISSION = 100

    fun checkAndRequestBluetoothAdminPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_ADMIN), REQUEST_CODE_BLUETOOTH_ADMIN_PERMISSION)
        }
    }

    private val REQUEST_CODE_BLUETOOTH_CONNECT_PERMISSION = 100

    fun checkAndRequestBluetoothConnectPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_CODE_BLUETOOTH_CONNECT_PERMISSION)
        }
    }

    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    fun handleBluetoothPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_BLUETOOTH_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(activity, "Bluetooth permission granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity, "Bluetooth permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    fun handleBluetoothAdminPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_BLUETOOTH_ADMIN_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(activity, "Bluetooth Admin permission granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity, "Bluetooth Admin permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    fun handleBluetoothConnectPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_BLUETOOTH_CONNECT_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(activity, "Bluetooth connect permission granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity, "Bluetooth connect permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_ENABLE_BLUETOOTH ->
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(activity, "Bluetooth is on", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity, "Could not turn on Bluetooth", Toast.LENGTH_LONG).show()
                }

            REQUEST_CODE_MAKE_DISCOVERABLE ->
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(activity, "Your device is now discoverable", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        activity,
                        "Failed to make device discoverable",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
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
            var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH)
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
            var intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            activity.startActivityForResult(intent, REQUEST_CODE_MAKE_DISCOVERABLE)
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

        activity.unregisterReceiver(bluetoothConnectionReceiver)
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
        activity.registerReceiver(bluetoothConnectionReceiver, filter)
    }

}



