package com.penguino.data.cache

import android.content.SharedPreferences
import com.penguino.data.local.models.DeviceInfo
import com.squareup.moshi.Moshi
import javax.inject.Inject

class DeviceInfoCacheImpl @Inject constructor(
	moshi: Moshi,
	private val prefs: SharedPreferences
) : DeviceInfoCache {
	companion object {
		const val SAVED_REG_INFO_KEY = "REG_INFO"
	}

	private val adapter = moshi.adapter(DeviceInfo::class.java)

	override fun getSelectedDevice(): DeviceInfo? {
		prefs.getString(SAVED_REG_INFO_KEY, null)?.let {
			return adapter.fromJson(it)
		} ?: return null
	}

	override fun saveSelectedDevice(regInfo: DeviceInfo) {
		prefs.edit().putString(SAVED_REG_INFO_KEY, adapter.toJson(regInfo)).apply()
	}

	override fun clearSelectedDevice() {
		prefs.edit().remove(SAVED_REG_INFO_KEY).apply()
	}

}