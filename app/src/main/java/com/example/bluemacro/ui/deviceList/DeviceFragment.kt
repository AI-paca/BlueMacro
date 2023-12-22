package com.example.bluemacro.ui.deviceList

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.bluemacro.bluetooth.GlobalPermissionRequester
import com.example.bluemacro.bluetooth.connection.BluetoothConnectionManager
import com.example.bluemacro.bluetooth.events.BluetoothEvent
import com.example.bluemacro.bluetooth.events.BluetoothEventService
import com.example.bluemacro.databinding.FragmentDeviceBinding
import com.example.bluemacro.bluetooth.devices.BlueMacroAccessibilityService

class DeviceFragment : Fragment(), BluetoothEvent {

    private var _binding: FragmentDeviceBinding? = null
    //var accessibillity:BlueMacroAccessibilityService?=null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val bluetoothEventReceiver = object : BroadcastReceiver() {
        //слушает широковещательные сообщения с действием "com.example.bluemacro.ACTION_BUTTON_PRESS"
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "com.example.bluemacro.ACTION_BUTTON_PRESS") {
                onButtonPress()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val textView: TextView = binding.textDevice
        val bluetoothManager = BluetoothConnectionManager(requireContext(), GlobalPermissionRequester(requireActivity()))
        bluetoothManager.activeDeviceName.observe(requireActivity(), Observer { deviceName ->
            textView.text = deviceName ?: "Device_name"
        })


        val intent = Intent(context, BluetoothEventService::class.java)
        val intentAccessibility = Intent(context, BlueMacroAccessibilityService::class.java)

        // Запустите сервисы в фоновом режиме
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
            requireContext().startForegroundService(intentAccessibility)
            Log.d("BluetoothEventService", "called")
        } else {
            requireContext().startService(intent)
            requireContext().startService(intentAccessibility)
            Log.d("BluetoothEventService", "called")
        }
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onButtonPress() {
        binding.switch1.text = "Not active";
        if (binding.switch1.isChecked) {
            changeBackgroundRandomly()
            //accessibillity?.startLongPress(100f,100f)
            //accessibillity?.stopLongPress()
            binding.switch1.text = "Active";
        }
    }


    fun changeBackgroundRandomly() {
        val random = java.util.Random()
        val color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
        binding.root.setBackgroundColor(color)
    }


    override fun onResume() {
        super.onResume()
        // Регистрируйте приемник при возобновлении фрагмента
        val intentFilter = IntentFilter("com.example.bluemacro.ACTION_BUTTON_PRESS")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(bluetoothEventReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        // Отмените регистрацию приемника при приостановке фрагмента
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(bluetoothEventReceiver)
    }

}
