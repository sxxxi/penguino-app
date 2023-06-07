package com.penguino.repositories.bluetooth

import com.penguino.models.DeviceInfo
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