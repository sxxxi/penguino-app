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
import androidx.core.app.ActivityCompat
import com.penguino.R
import com.penguino.databinding.FragmentDevicesFoundBinding
import com.penguino.databinding.FragmentPenguinoScanBinding


/*
TODO: Display list of devices, then pass the selected item to "verify" fragment, before passing to "connect" fragment
 */

private const val ARG_PARAM1 = "SCANNED_DEVICES"
private const val TAG = "PenguinoScanResultFragment"
class DevicesFoundFragment : Fragment() {
    private lateinit var binding: FragmentDevicesFoundBinding
    private var scannedDevices: HashSet<BluetoothDevice>? = HashSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scannedDevices = it.getSerializable(ARG_PARAM1, HashSet::class.java) as HashSet<BluetoothDevice>
        }

        // Check if fragment is receiving the list of devices.
        scannedDevices?.let {
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
                    return
                }
                Log.d(TAG, dev.name)
                return@all true

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDevicesFoundBinding.inflate(inflater)
        return binding.root
    }
}