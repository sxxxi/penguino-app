package com.penguino.data.repositories.bluetooth

import com.penguino.data.local.models.DeviceInfo
import kotlinx.coroutines.flow.StateFlow

interface BleRepository {
    fun bindService()
    fun unbindService()

    suspend fun sendMessage(message: String)
    fun connect(address: String): Boolean
    fun disconnect()

    fun btEnabled(): Boolean
}