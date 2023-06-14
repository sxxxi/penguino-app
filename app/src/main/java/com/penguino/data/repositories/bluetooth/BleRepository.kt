package com.penguino.data.repositories.bluetooth

import com.penguino.data.local.models.DeviceInfo
import kotlinx.coroutines.flow.StateFlow

interface BleRepository {
    val deviceList: StateFlow<List<DeviceInfo>>
    val btEnabled: StateFlow<Boolean>
    val scanning: StateFlow<Boolean>

    fun scanDevices()
    fun stopScan()
    fun bindService()
    fun unbindService()

    fun sendMessage(message: String)
    fun connect(device: DeviceInfo): Boolean
    fun disconnect()

    fun btEnabled(): Boolean
}