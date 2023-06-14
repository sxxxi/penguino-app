package com.penguino.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.penguino.data.local.models.RegistrationInfo
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DeviceDao {
	@Query("SELECT * FROM RegistrationInfo")
	abstract fun getAll(): Flow<List<RegistrationInfo>>

	@Insert
	abstract fun saveDevice(regInfo: RegistrationInfo)

	@Delete
	abstract fun removeDevice(regInfo: RegistrationInfo)

	@Query("SELECT COUNT(*) FROM (SELECT * FROM RegistrationInfo WHERE address = :address)")
	abstract fun deviceExists(address: String): Boolean

}