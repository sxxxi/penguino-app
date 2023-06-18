package com.penguino.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.penguino.data.local.models.RegistrationInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DeviceDao {
	@Query("SELECT * FROM RegistrationInfoEntity")
	abstract fun getAll(): Flow<List<RegistrationInfoEntity>>

	@Insert
	abstract fun saveDevice(regInfo: RegistrationInfoEntity)

	@Query("DELETE FROM RegistrationInfoEntity WHERE address = :id")
	abstract fun removeDeviceById(id: String)

	@Query("SELECT COUNT(*) FROM (SELECT * FROM RegistrationInfoEntity WHERE address = :address)")
	abstract fun deviceExists(address: String): Boolean

}