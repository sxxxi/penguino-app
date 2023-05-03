package com.penguino.bluetooth.services

import android.bluetooth.BluetoothAdapter
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.penguino.bluetooth.models.DeviceInfo

interface BluetoothManagement {
    val devicesFound: SnapshotStateList<DeviceInfo>
    val scanning: MutableState<Boolean>
    fun scanDevices()
    fun stopScan()
    fun bindService()
    fun unbindService()

    fun sendMessage(message: String)
    fun connect(device: DeviceInfo): Boolean
    fun disconnect()
}