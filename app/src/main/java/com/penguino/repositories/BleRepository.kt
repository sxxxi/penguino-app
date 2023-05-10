package com.penguino.repositories

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.penguino.models.DeviceInfo

interface BleRepository {
    val devicesFound: SnapshotStateList<DeviceInfo>
    val scanning: MutableState<Boolean>
    fun scanDevices()
    fun stopScan()
    fun bindService()
    fun unbindService()

    fun sendMessage(message: String)
    fun connect(device: DeviceInfo): Boolean
    fun disconnect()

    fun btEnabled(): Boolean
}