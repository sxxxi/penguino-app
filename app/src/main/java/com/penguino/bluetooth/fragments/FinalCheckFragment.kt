package com.penguino.bluetooth.fragments

import android.bluetooth.BluetoothClass.Device
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.penguino.R
import com.penguino.bluetooth.models.DeviceInfo
import com.penguino.bluetooth.services.BluetoothLeService
import com.penguino.databinding.FragmentFinalCheckBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "SELECTED_DEVICE"

/**
 * A simple [Fragment] subclass.
 * Use the [FinalCheckFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val TAG = "FinalCheckFragment"
class FinalCheckFragment : Fragment() {
    private lateinit var binding: FragmentFinalCheckBinding
    private var selectedDevice: DeviceInfo? = null
    private var bluetoothLeService: BluetoothLeService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDevice = it.getSerializable(ARG_PARAM1, DeviceInfo::class.java)
        }

        // Bind service here
        Intent(requireActivity(), BluetoothLeService::class.java).also { intent ->
            val bindSuccess: Boolean = requireActivity().bindService(intent, bluetoothServiceConn, Context.BIND_AUTO_CREATE)
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
        binding = FragmentFinalCheckBinding.inflate(layoutInflater)

        selectedDevice?.let {
            Log.d(TAG, it.toString())
        }


        return binding.root
    }

    // Callbacks for binding BluetoothLeService
    private val bluetoothServiceConn = object: ServiceConnection {
        private val TAG = "BluetoothServiceConnection"
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "Service Bound")
            bluetoothLeService = (service as BluetoothLeService.ServiceBinder).getService()
            Log.d(TAG, "Service null: ${bluetoothLeService == null}")
            bluetoothLeService?.let { bluetooth ->
                // Connect here and do stuff here on service created
                if (!bluetooth.initialize()) {
                    Log.d(TAG, "Unable to initialize service")
                    requireActivity().finish()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Service Unbound")
        }
    }
}