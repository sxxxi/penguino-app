package com.penguino.bluetooth.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.penguino.R
import com.penguino.bluetooth.services.BluetoothLeService
import com.penguino.databinding.FragmentPenguinoScanBinding


/**
 * This fragment is for getting the list of devices nearby
 * passing it to next fragment.
 */
private const val DTAG = "PenguinoScanFragment"
class PenguinoScanFragment : Fragment() {
    private lateinit var binding: FragmentPenguinoScanBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bleScanner: BluetoothLeScanner? = null
    private val devices = HashSet<BluetoothDevice>()
    private var scanning: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val btMan = requireActivity().getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = btMan.adapter

        // Check if bluetooth is enabled
        if (!bluetoothAdapter.isEnabled) {
            // popup to inform app needs bt
            requireActivity().finish()
        }

        bleScanner = bluetoothAdapter.bluetoothLeScanner
        scanBle()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPenguinoScanBinding.inflate(inflater)
        return binding.root
    }



    // BluetoothLE scan callbacks
    private val scanCallback: ScanCallback = object: ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            result?.device?.let { dev ->
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                }
                if (dev.name == "Penguino") {
                    devices.add(dev)
                }

            }
        }
    }

    private fun scanBle() {
        if (scanning) return
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }

        bleScanner?.let { scanner ->
            scanner.startScan(scanCallback)
            Log.d(DTAG, "Scanning")
            scanning = true
            Handler(Looper.getMainLooper()).postDelayed({ ->
                Log.d(DTAG, "Stopping scan")
                scanner.stopScan(scanCallback)
                scanning = false

                if (devices.size > 0) {
                    val bndl = bundleOf("SCANNED_DEVICES" to devices)
//                    findNavController().navigate(R.id.action_penguinoScanFragment_to_penguinoScanResultFragment, bndl)
                    findNavController().navigate(R.id.action_penguinoScanFragment_to_devicesFoundFragment)
                } else {
                    findNavController().navigate(R.id.action_penguinoScanFragment_to_bluetoothDevicesScan)
                }
            }, 3000)
        }

    }


}