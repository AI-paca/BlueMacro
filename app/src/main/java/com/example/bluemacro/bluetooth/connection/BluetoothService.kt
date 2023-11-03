package com.example.bluemacro.bluetooth.connection

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.bluemacro.bluetooth.GlobalPermissionRequester

class BluetoothService : Service() {
    private lateinit var bluetoothConnectionManager: BluetoothConnectionManager

    companion object {
        private var instance: BluetoothService? = null

        fun getInstance(): BluetoothService {
            if (instance == null) {
                instance = BluetoothService()
            }
            return instance!!
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Здесь вы можете обработать команды старта сервиса
        Log.d("BluetoothService", "Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Здесь вы можете выполнить любую очистку перед уничтожением сервиса
        Log.d("BluetoothService", "Service destroyed")
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    fun initializeBluetoothService(activity: Activity) {
        bluetoothConnectionManager = BluetoothConnectionManager(this, GlobalPermissionRequester(activity))
    }

    fun enableBluetooth(activity: Activity) {
        bluetoothConnectionManager.enableBluetooth(activity)
    }

    fun disableBluetooth() {
        bluetoothConnectionManager.disableBluetooth()
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothConnectionManager.isBluetoothEnabled()
    }

    fun getPairedDevices(): Set<BluetoothDevice> {
        return bluetoothConnectionManager.getPairedDevices()
    }

    fun makeDiscoverable(activity: Activity) {
        bluetoothConnectionManager.makeDiscoverable(activity)
    }

    fun getConnectedDevices(): List<String> {
        return bluetoothConnectionManager.getConnectedDevices()
    }

    fun registerReceiver() {
        bluetoothConnectionManager.registerBluetoothConnectionReceiver()
    }

    fun unregisterReceiver() {
        bluetoothConnectionManager.unregisterBluetoothConnectionReceiver()
    }

    fun updateConnectedDevicesList() {
        bluetoothConnectionManager.updateConnectedDevicesList()
    }

    fun connectedDevicesList(): LiveData<List<String>> {
        return bluetoothConnectionManager.connectedDevicesList
    }

    fun isBluetoothAvailable(): Boolean {
        return bluetoothConnectionManager.isBluetoothAvailable()
    }

    fun isDiscovering(): Boolean {
        return bluetoothConnectionManager.isDiscovering()
    }
    fun checkAndRequestPermission() {
        bluetoothConnectionManager.checkAndRequestBluetoothConnectPermission()
    }

}
