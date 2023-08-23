package com.penguino.data.bluetooth.contracts

interface BluetoothConnection {
	fun connect(address: String, autoConnect: Boolean = false)
	fun disconnect()
	fun reconnect()
	fun close()
}