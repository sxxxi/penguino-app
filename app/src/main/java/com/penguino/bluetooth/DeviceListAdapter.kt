package com.penguino.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.penguino.R

class DeviceListAdapter(
    private val ctx: Context,
    private val devices: ArrayList<BluetoothDevice>,
    private val deviceSelectHandler: () -> Unit
    ): RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    class ViewHolder(
        view: View,
        private val deviceSelectHandler: () -> Unit
    ): RecyclerView.ViewHolder(view) {
        val deviceName: TextView
        val deviceAddress: TextView

        init {
            deviceName = view.findViewById(R.id.text_device_name)
            deviceAddress = view.findViewById(R.id.text_device_address)
            view.setOnClickListener {
                deviceSelectHandler()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_device_list, parent, false)
        return ViewHolder(layout, deviceSelectHandler)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (ActivityCompat.checkSelfPermission(
                this.ctx,
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
        holder.deviceName.text = devices[position].name
        holder.deviceAddress.text = devices[position].address
    }
}