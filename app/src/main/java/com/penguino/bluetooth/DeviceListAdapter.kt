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
import com.penguino.bluetooth.models.DeviceInfo

class DeviceListAdapter(
    private val ctx: Context,
    private val devices: ArrayList<DeviceInfo>,
    private val deviceSelectHandler: (View, DeviceInfo) -> Unit
    ): RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    class ViewHolder(
        view: View,
        private val deviceSelectHandler: (View, DeviceInfo) -> Unit
    ): RecyclerView.ViewHolder(view) {
        lateinit var deviceInfo: DeviceInfo
        val deviceName: TextView
        val deviceAddress: TextView

        init {
            deviceName = view.findViewById(R.id.text_device_name)
            deviceAddress = view.findViewById(R.id.text_device_address)
            view.setOnClickListener { v ->
                deviceSelectHandler(v, deviceInfo)
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
        holder.deviceInfo = devices[position]
        holder.deviceInfo.let {
            holder.deviceName.text = it.name
            holder.deviceAddress.text = it.address
        }


    }
}