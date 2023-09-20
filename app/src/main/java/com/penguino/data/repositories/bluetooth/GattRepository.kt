package com.penguino.data.repositories.bluetooth

import kotlinx.coroutines.flow.StateFlow

interface GattRepository {
	val connectionState: StateFlow<Int>

	fun bindService()
	fun unbindService()

	suspend fun sendMessage(message: String)
	fun connect(address: String): Boolean
	fun disconnect()

}