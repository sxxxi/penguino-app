package com.penguino.data.utils

import java.time.Instant
import java.util.Calendar
import java.util.Date

class EpochFactory {
	companion object {
		fun dateToEpochSeconds(date: Date): Long {
			return date.toInstant().epochSecond
		}
		fun epochSecondsToDate(epochSeconds: Long): Date {
			return Date.from(Instant.ofEpochSecond(epochSeconds))
		}
		fun currentEpochSeconds(): Long {
			return Date().toInstant().epochSecond
		}
		fun epochToCalendar(epoch: Long): Calendar {
			val calendar = Calendar.getInstance()
			val date = epochSecondsToDate(epoch)
			calendar.time = date
			return calendar
		}
	}
}