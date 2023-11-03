package com.example.bluemacro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bluemacro.databinding.FragmentHomeBinding
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.bluemacro.bluetooth.connection.BluetoothConnectionManager
import com.example.bluemacro.R
import com.example.bluemacro.bluetooth.GlobalPermissionRequester


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bluetoothManager: BluetoothConnectionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        bluetoothManager = BluetoothConnectionManager(requireContext(), GlobalPermissionRequester(requireActivity()))

        bluetoothManager.registerBluetoothConnectionReceiver()
        bluetoothManager.updateConnectedDevicesList()

        lateinit var statusBluetooth: TextView
        lateinit var paired: TextView
        lateinit var on_btn: Button
        lateinit var off_btn: Button
        lateinit var discoverable_btn: Button
        lateinit var paired_btn: Button
        lateinit var bluetooth_img: ImageView
        lateinit var deviceList: TextView

        on_btn = root.findViewById(R.id.on_bluetooth)
        off_btn = root.findViewById(R.id.off_bluetooth)
        discoverable_btn = root.findViewById(R.id.discoverable_btn)
        statusBluetooth = root.findViewById(R.id.status_bluetooth)
        paired_btn = root.findViewById(R.id.paired_btn)
        paired = root.findViewById(R.id.paired)
        deviceList = root.findViewById(R.id.device_list)

        bluetoothManager.connectedDevicesList.observe(viewLifecycleOwner) { devices ->
            if (devices.isEmpty()) {
                deviceList.text = "No devices connected"
            } else {
                deviceList.text = devices.joinToString("\n")
            }
        }


        if (!bluetoothManager.isBluetoothAvailable())
            statusBluetooth.text = "Bluetooth is unavailable"
        else
            statusBluetooth.text = "Bluetooth is available"

        deviceList = root.findViewById(R.id.device_list)
        bluetoothManager.connectedDevicesList.observe(viewLifecycleOwner) { devices ->
            if (devices.isEmpty()) {
                deviceList.text = "No devices connected"
            } else {
                deviceList.text = devices.joinToString("\n")
            }
        }


        on_btn.setOnClickListener {
            if (bluetoothManager.isBluetoothEnabled()) {
                Toast.makeText(requireContext(), "Bluetooth is already ON", Toast.LENGTH_LONG).show()
            } else {
                bluetoothManager.enableBluetooth(requireActivity())
            }
        }

        off_btn.setOnClickListener {
            if (!bluetoothManager.isBluetoothEnabled()) {
                Toast.makeText(requireContext(), "Bluetooth is already OFF", Toast.LENGTH_LONG).show()
            } else {
                bluetoothManager.disableBluetooth()
                Toast.makeText(requireContext(), "Bluetooth turned off", Toast.LENGTH_LONG).show()
            }
        }

        discoverable_btn.setOnClickListener {
            if (!bluetoothManager.isDiscovering()) {
                Toast.makeText(requireContext(), "making your device discoverable", Toast.LENGTH_LONG).show()
                bluetoothManager.makeDiscoverable(requireActivity())
            }
        }

        paired_btn.setOnClickListener {
            if (bluetoothManager.isBluetoothEnabled()) {
                bluetoothManager.checkAndRequestBluetoothConnectPermission()
                paired.text = "Paired Devices"
                val devices = bluetoothManager.getPairedDevices()
                for (device in devices) {
                    val deviceName = device.name
                    val deviceAddress = device.address
                    paired.append("\nDevice: $deviceName , $deviceAddress")
                }
            } else {
                Toast.makeText(requireContext(), "turn on bluetooth first", Toast.LENGTH_LONG).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bluetoothManager.unregisterBluetoothConnectionReceiver()
        _binding = null
    }
}

