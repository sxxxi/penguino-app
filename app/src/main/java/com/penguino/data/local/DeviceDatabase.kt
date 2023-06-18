package com.penguino.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.local.dao.DeviceDao

@Database(entities = [RegistrationInfoEntity::class], version = 2)
abstract class DeviceDatabase: RoomDatabase() {
	abstract fun dao(): DeviceDao
}