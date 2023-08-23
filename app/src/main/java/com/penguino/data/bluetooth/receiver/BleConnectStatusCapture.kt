package com.penguino.data.bluetooth.receiver

import kotlinx.coroutines.flow.StateFlow

interface BleConnectStatusCapture {
	val connectionStatus: StateFlow<Int>
	fun captureBleStatus(action: String) : Int
}