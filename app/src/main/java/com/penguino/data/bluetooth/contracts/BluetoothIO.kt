package com.penguino.data.bluetooth.contracts

interface BluetoothIO {
	fun read()
	fun write(message: String)
}