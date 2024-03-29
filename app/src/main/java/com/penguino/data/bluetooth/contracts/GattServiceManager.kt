package com.penguino.data.bluetooth.contracts

import kotlinx.coroutines.flow.StateFlow

interface GattServiceManager {
	val leService: StateFlow<LeService?>
	val connectionState: StateFlow<Int>

	fun bind()
	fun unbind()
}