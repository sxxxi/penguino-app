package com.penguino.bluetooth.fragments

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.penguino.R
import com.penguino.bluetooth.services.BluetoothLeService
import com.penguino.databinding.FragmentBluetoothDevicesScanBinding
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val DTAG = "BluetoothDevicesScanFragment"




/**
 * A simple [Fragment] subclass.
 * Use the [BluetoothDevicesScan.newInstance] factory method to
 * create an instance of this fragment.
 */
class BluetoothDevicesScan : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentBluetoothDevicesScanBinding
    private var device: BluetoothDevice? = null
    private var bluetoothLeService: BluetoothLeService? = null
    private var scanning: Boolean = false

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
//                Log.d(DTAG, "Permission granted")
            }
        }

    // A popup for granting access. This is temporary and I am looking for better alternatives.
    private val multiplePermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            results.forEach {
                Log.d("BOOBA", "${it.key}: ${it.value}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_bluetooth_devices_scan, container, false)
        binding = FragmentBluetoothDevicesScanBinding.inflate(inflater)

        // Ask permissions DO SOMETHING ABOUT THIS!!!
        multiplePermissionLauncher.launch(arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ))

        // Bind service. Unbind connection when binding fails
        Log.d(DTAG, "Trying to init service")
        Intent(requireActivity(), BluetoothLeService::class.java).also { intent ->
            val bindSuccess: Boolean = requireActivity().bindService(intent, bluetoothServiceConn, Context.BIND_AUTO_CREATE)
            Log.d(DTAG, bindSuccess.toString())
            if (!bindSuccess) {
                requireActivity().unbindService(bluetoothServiceConn)
            }
        }

        // Only enable connect button when Penguino is found.
        binding.connect.isEnabled = false;

        binding.buttonScan.setOnClickListener {
            scanBle()
        }

        binding.connect.setOnClickListener {
            bluetoothLeService?.connect(device!!.address)
        }

        binding.buttonDisconnect.setOnClickListener {
            bluetoothLeService?.disconnect()
        }

        binding.buttonServices.setOnClickListener {
            Log.d(DTAG, "BLE service null: ${bluetoothLeService == null}")
            return@setOnClickListener
            bluetoothLeService?.getGattServices()?.forEach {
                Log.d("SERVICES", "${it?.uuid.toString()}: ${it?.uuid == UUID.fromString("00001800-0000-1000-8000-00805f9b34fb")}")
            }
        }

        binding.ledOn.setOnClickListener {
            Log.d("BOOOO", (bluetoothLeService == null).toString())
            bluetoothLeService?.writeToPengu("ON")
        }

        binding.ledOff.setOnClickListener {
            bluetoothLeService?.writeToPengu("OFF")
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(gattUpdateCallback, makeGattUpdateIntentFilter())
        if (bluetoothLeService != null) {
            val result = bluetoothLeService?.connect(device!!.address)
            Log.d(DTAG, "Connect request result=$result")
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(gattUpdateCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unbindService(bluetoothServiceConn)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BluetoothDevicesScan.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BluetoothDevicesScan().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // BluetoothLE scan callbacks
    private val scanCallback: ScanCallback = object: ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            val d: BluetoothDevice? = result?.device
            if (d != null) {
                val name = result.scanRecord?.deviceName
//                Log.d(DTAG, "$name: ${d.address}")
                if (name == "Penguino") {
                    val btMgr = requireActivity().getSystemService(BluetoothManager::class.java)
                    val scanner = btMgr?.adapter?.bluetoothLeScanner
                    Log.d("TEM", "FOUND PENGU!")
                    device = result.device
                    binding.connect.isEnabled = true

                    if (activity?.let {
                            ActivityCompat.checkSelfPermission(
                                it,
                                Manifest.permission.BLUETOOTH_SCAN
                            )
                        } != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        scanner?.stopScan(this)
                        return
                    }

                }
            }
        }
    }

    // Callbacks for BluetoothLeService events (connected, disconnected, etc.)
    private val bluetoothServiceConn = object: ServiceConnection {
        private val TAG = "BluetoothServiceConnection"
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "Service Bound")
            bluetoothLeService = (service as BluetoothLeService.ServiceBinder).getService()
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

    // This are the callbacks for sending information between the fragment/activity and the service.
    private val gattUpdateCallback: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    Toast.makeText(requireActivity(), "Yooo! Im connected!", Toast.LENGTH_SHORT).show()
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    Toast.makeText(requireActivity(), "Disconnected", Toast.LENGTH_SHORT).show()
                }
                BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED -> {
                    val services: List<BluetoothGattService?>? = bluetoothLeService?.getGattServices()
                    services?.let { s ->
                        s.forEach { service ->
                            Log.d("SERVICES", "${service?.uuid}: ${service.toString()}")
                        }
                    }
                }
            }
        }
    }

    private fun scanBle() {
        if (scanning) return

        val btMgr = requireActivity().getSystemService(BluetoothManager::class.java)
        val scanner = btMgr.adapter.bluetoothLeScanner

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)

        }

//        scanner.startScan(filter, ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_OPPORTUNISTIC).build(), scanCallback)
        scanner.startScan(scanCallback)
        Log.d(DTAG, "Scanning")
        scanning = true
        Handler(Looper.getMainLooper()).postDelayed({ ->
            Log.d(DTAG, "Stopping scan")
            scanner.stopScan(scanCallback)
            scanning = false

        }, 3000)
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter? {
        return IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        }
    }
}