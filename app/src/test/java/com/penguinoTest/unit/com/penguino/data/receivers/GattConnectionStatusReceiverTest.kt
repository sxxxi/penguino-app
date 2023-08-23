package com.penguinoTest.unit.com.penguino.data.receivers

import android.content.Intent
import com.penguino.data.bluetooth.PenguinoGattService
import com.penguino.data.bluetooth.receiver.GattConnectionStatusReceiver
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GattConnectionStatusReceiverTest {
	private val receiver = GattConnectionStatusReceiver()

	@Test
	fun testReceiverState() = runBlocking {
		val actionString = PenguinoGattService.ACTION_GATT_CONNECTED
		val target = PenguinoGattService.STATE_GATT_CONNECTED

		val mIntent = mock(Intent::class.java)
		`when`(mIntent.action).thenReturn(actionString)

		receiver.onReceive(null, mIntent)
		val actual: Int = receiver.connectionStatus.value

		assert(
			actual == target
		) { "action: $actionString, target: $target, actual: $actual" }

	}
}
