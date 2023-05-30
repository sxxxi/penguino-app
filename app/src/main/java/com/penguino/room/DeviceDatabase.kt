package com.penguino.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.penguino.models.RegistrationInfo
import com.penguino.room.dao.DeviceDao

@Database(entities = [RegistrationInfo::class], version = 1)
abstract class DeviceDatabase: RoomDatabase() {
	abstract fun dao(): DeviceDao
}