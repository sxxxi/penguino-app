package com.penguino.bluetooth.fragments

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.penguino.R
import com.penguino.bluetooth.DeviceListAdapter
import com.penguino.databinding.FragmentDevicesFoundBinding
import com.penguino.databinding.FragmentPenguinoScanBinding


/*
TODO: Display list of devices, then pass the selected item to "verify" fragment, before passing to "connect" fragment
 */

private const val ARG_PARAM1 = "SCANNED_DEVICES"
private const val TAG = "PenguinoScanResultFragment"
class DevicesFoundFragment : Fragment() {
    private lateinit var binding: FragmentDevicesFoundBinding
    private var scannedDevices: ArrayList<BluetoothDevice>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scannedDevices = it.getSerializable(ARG_PARAM1, ArrayList::class.java) as ArrayList<BluetoothDevice>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDevicesFoundBinding.inflate(inflater)

        // Check if fragment is receiving the list of devices.
        scannedDevices?.let {
            binding.deviceList.adapter = DeviceListAdapter(requireContext(), it) {
                findNavController().navigate()
            }
            binding.deviceList.layoutManager = LinearLayoutManager(requireContext())
            it.all { dev ->
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@let
                }
                Log.d(TAG, dev.name)
                return@all true
            }
        }
        return binding.root
    }
}