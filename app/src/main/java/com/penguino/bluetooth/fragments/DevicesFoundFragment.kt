package com.penguino.bluetooth.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.penguino.R
import com.penguino.bluetooth.DeviceListAdapter
import com.penguino.bluetooth.models.DeviceInfo
import com.penguino.databinding.FragmentDevicesFoundBinding


/*
TODO: Display list of devices, then pass the selected item to "verify" fragment, before passing to "connect" fragment
 */

private const val ARG_PARAM1 = "SCANNED_DEVICES"
private const val TAG = "PenguinoScanResultFragment"
class DevicesFoundFragment : Fragment() {
    private lateinit var binding: FragmentDevicesFoundBinding
    private var scannedDevices: ArrayList<DeviceInfo>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scannedDevices = it.getSerializable(ARG_PARAM1, ArrayList::class.java) as ArrayList<DeviceInfo>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDevicesFoundBinding.inflate(inflater)

        // Check if fragment is receiving the list of devices.
        scannedDevices?.let { devices ->
            binding.deviceList.adapter = DeviceListAdapter(requireContext(), devices) { _, deviceInfo ->
                val bundle = bundleOf("SELECTED_DEVICE" to deviceInfo)
                findNavController().navigate(R.id.action_devicesFoundFragment_to_registerNameFragment, bundle)
            }
            binding.deviceList.layoutManager = LinearLayoutManager(requireContext())
            devices.all { dev ->
                Log.d(TAG, dev.name)
                return@all true
            }
        }
        return binding.root
    }
}