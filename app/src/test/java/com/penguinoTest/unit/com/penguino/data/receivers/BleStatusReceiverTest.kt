package com.penguinoTest.unit.com.penguino.data.receivers

import android.content.Intent
import com.penguino.data.local.BleServiceDataSource
import com.penguino.data.receivers.BleStatusReceiver
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BleStatusReceiverTest {
	private val receiver = BleStatusReceiver()

	@Test
	fun testReceiverState() = runBlocking {
		val actionString = BleServiceDataSource.ACTION_GATT_CONNECTED
		val target = BleServiceDataSource.STATE_CONNECTED

		val mIntent = mock(Intent::class.java)
		`when`(mIntent.action).thenReturn(actionString)

		receiver.onReceive(null, mIntent)
		val actual: Int = receiver.connectionState.value

		assert(
			actual == target
		) { "action: $actionString, target: $target, actual: $actual" }

	}
}
