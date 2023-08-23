package com.penguino.data.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import javax.inject.Inject
import kotlin.random.Random

/**
 *
 */
class NotificationTool @Inject constructor(private val context: Context) {
	private val nm = context.getSystemService(NotificationManager::class.java)

	fun getOrCreateChannel(channel: NotificationChannel): NotificationChannel {
		if (nm.getNotificationChannel(channel.id) == null) {
			nm.createNotificationChannel(channel)
		}
		return channel
	}

	fun generateNotificationId(): Int {
		return Random.nextInt()
	}

	fun createNotification(
		channel: NotificationChannel,
		builder: (Context, NotificationCompat.Builder) -> NotificationCompat.Builder
	): Notification {
		return builder(context, NotificationCompat.Builder(context, channel.id)).build()
	}

	fun postNotification(
		channel: NotificationChannel,
		notificationId: Int = generateNotificationId(),
		notificationBuilder: (Context, NotificationCompat.Builder) -> NotificationCompat.Builder
	): Int {
		return getOrCreateChannel(channel).let {
			nm.notify(
				it.id,
				notificationId,
				notificationBuilder(
					context,
					NotificationCompat.Builder(context, channel.id)
				).build()
			)
			notificationId
		}
	}

	fun postNotification(
		channel: NotificationChannel,
		notificationId: Int = generateNotificationId(),
		notification: Notification
	): Int {
		return getOrCreateChannel(channel).let {
			nm.notify(
				it.id,
				notificationId,
				notification
			)
			notificationId
		}
	}

	fun cancelNotification(id: Int) {
		nm.cancel(id)
	}
}

object NotificationChannels {
	val BLUETOOTH_CONNECTION by lazy {
		NotificationChannel(
			"bluetooth_notification",
			"Bluetooth notifications",
			NotificationManager.IMPORTANCE_DEFAULT
		)
	}
}