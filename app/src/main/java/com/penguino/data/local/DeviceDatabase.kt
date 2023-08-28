package com.penguino.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.penguino.data.local.dao.DeviceDao
import com.penguino.data.local.models.RegistrationInfoEntity

@Database(entities = [RegistrationInfoEntity::class], version = 1)
abstract class DeviceDatabase: RoomDatabase() {
	abstract fun dao(): DeviceDao
}