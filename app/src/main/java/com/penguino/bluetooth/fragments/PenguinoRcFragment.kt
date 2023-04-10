package com.penguino.bluetooth.fragments

import android.bluetooth.BluetoothGattService
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.penguino.R
import com.penguino.bluetooth.models.DeviceInfo
import com.penguino.bluetooth.services.BluetoothLeService
import com.penguino.databinding.FragmentPenguinoRcBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "SELECTED_DEVICE"

/**
 * A simple [Fragment] subclass.
 * Use the [PenguinoRcFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val TAG = "FinalCheckFragment"
class PenguinoRcFragment : Fragment() {
    private lateinit var binding: FragmentPenguinoRcBinding
    private var selectedDevice: DeviceInfo? = null
    private var bluetoothLeService: BluetoothLeService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDevice = it.getSerializable(ARG_PARAM1, DeviceInfo::class.java)
        }

        // Bind service here
        Intent(requireActivity(), BluetoothLeService::class.java).also { intent ->
            val bindSuccess: Boolean = requireActivity().bindService(
                intent,
                bluetoothServiceConn,
                Context.BIND_AUTO_CREATE
            )
            Log.d(TAG, bindSuccess.toString())
            if (!bindSuccess) {
                requireActivity().unbindService(bluetoothServiceConn)
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPenguinoRcBinding.inflate(layoutInflater)

        selectedDevice?.let {
            Log.d(TAG, it.toString())
        }

        binding.ledOn.setOnClickListener {
            bluetoothLeService?.writeToPengu("ON")
        }

        binding.ledOff.setOnClickListener {
            bluetoothLeService?.writeToPengu("OFF")
        }

        // Only enable when is connected.
        binding.buttonDisconnect.setOnClickListener {
            bluetoothLeService?.disconnect()
            findNavController().navigate(R.id.action_penguinoRcFragment_to_homeFragment)
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(gattUpdateCallback)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(gattUpdateCallback, makeGattUpdateIntentFilter())
        bluetoothLeService?.let { bleService ->
            selectedDevice?.address?.let { btDevAddress ->
                val result = bleService.connect(btDevAddress)
                Log.d(TAG, "Connect request result=$result")
            }
        }
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unbindService(bluetoothServiceConn)
    }

    // Callbacks for binding BluetoothLeService
    private val bluetoothServiceConn = object : ServiceConnection {
        private val TAG = "BluetoothServiceConnection"
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i(TAG, "BluetoothLeService Bound")
            bluetoothLeService = (service as BluetoothLeService.ServiceBinder).getService()
            bluetoothLeService?.let { bluetooth ->
                // Connect here and do stuff here on service created
                if (!bluetooth.initialize()) {
                    Log.e(TAG, "Unable to initialize service")
                    requireActivity().finish()
                }
            }

            var connectStatus = false
            // Connect.
            selectedDevice?.let { dev ->
                connectStatus = bluetoothLeService?.connect(dev.address) ?: false
                binding.buttonDisconnect.isEnabled = connectStatus
            }

            Log.i(TAG, "Connected: $connectStatus")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Service Unbound")
        }
    }

    // This are the callbacks for sending information between the fragment/activity and the service.
    private val gattUpdateCallback: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    Toast.makeText(requireActivity(), "Yooo! Im connected!", Toast.LENGTH_SHORT)
                        .show()
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    Toast.makeText(requireActivity(), "Disconnected", Toast.LENGTH_SHORT).show()
                }
                BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED -> {
                    val services: List<BluetoothGattService?>? =
                        bluetoothLeService?.getGattServices()
                    services?.let { s ->
                        s.forEach { service ->
                            Log.d("SERVICES", "${service?.uuid}: ${service.toString()}")
                        }
                    }
                }
            }
        }

    }
}