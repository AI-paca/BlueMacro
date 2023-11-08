package com.example.bluemacro.ui.deviceList

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.bluemacro.bluetooth.events.BluetoothEvent
import com.example.bluemacro.bluetooth.events.BluetoothEventService
import com.example.bluemacro.databinding.FragmentDeviceBinding

class DeviceFragment : Fragment(), BluetoothEvent {

    private var _binding: FragmentDeviceBinding? = null



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
        val deviceViewModel =
            ViewModelProvider(this).get(DeviceViewModel::class.java)

        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDevice
        deviceViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val intent = Intent(context, BluetoothEventService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
            Log.d("BluetoothEventService", "called")
        } else {
            requireContext().startService(intent)
            Log.d("BluetoothEventService", "called")
        }




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onButtonPress() {
        binding.switch1.text = "fgd";
        if (!binding.switch1.isChecked) {
            binding.switch1.text = "ggggg";
        }
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
