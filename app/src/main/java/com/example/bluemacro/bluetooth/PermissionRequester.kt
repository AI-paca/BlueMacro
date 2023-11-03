package com.example.bluemacro.bluetooth

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
interface PermissionRequester {
    fun requestBluetoothPermission()
    fun requestBluetoothAdminPermission()
    fun requestBluetoothConnectPermission()
}

class GlobalPermissionRequester(private val activity: Activity) : PermissionRequester {
    private val REQUEST_CODE_ENABLE_BLUETOOTH = 1
    private val REQUEST_CODE_MAKE_DISCOVERABLE = 2

    override fun requestBluetoothPermission() {
        if (!activity.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            return
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH), REQUEST_CODE_ENABLE_BLUETOOTH)
        }
    }

    override fun requestBluetoothAdminPermission() {
        if (!activity.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            return
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_ADMIN), REQUEST_CODE_MAKE_DISCOVERABLE)
        }
    }

    override fun requestBluetoothConnectPermission() {
        if (!activity.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            return
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_CODE_ENABLE_BLUETOOTH)
        }
    }
}
