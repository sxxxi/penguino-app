package com.penguino.data.bluetooth.receivers

import android.content.Intent
import com.penguino.data.bluetooth.PenguinoGattService
import com.penguino.data.bluetooth.receiver.GattConnectionStatusReceiver
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class GattConnectionStatusReceiverTest {
	private val receiver = GattConnectionStatusReceiver()

	@ParameterizedTest
	@CsvSource(
		"${PenguinoGattService.ACTION_GATT_CONNECTED},${PenguinoGattService.STATE_GATT_CONNECTED}",
		"${PenguinoGattService.ACTION_GATT_CONNECTING},${PenguinoGattService.STATE_GATT_CONNECTING}",
		"${PenguinoGattService.ACTION_GATT_DISCONNECTED},${PenguinoGattService.STATE_GATT_DISCONNECTED}",
		"${PenguinoGattService.ACTION_GATT_DISCONNECTING},${PenguinoGattService.STATE_GATT_DISCONNECTING}",
	)
	fun `Test receiver converting actions to appropriate states`(input: String, output: Int) = runBlocking {
		val mIntent = mock(Intent::class.java)
		`when`(mIntent.action).thenReturn(input)

		receiver.onReceive(null, mIntent)
		val actual: Int = receiver.connectionStatus.value

		assert(
			actual == output
		) { "action: $input, target: $output, actual: $actual" }

	}
}
