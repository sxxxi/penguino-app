package com.penguino.cache

import android.content.SharedPreferences
import com.penguino.models.RegistrationInfo
import com.squareup.moshi.Moshi
import javax.inject.Inject

class RegInfoCacheImpl @Inject constructor(
	moshi: Moshi,
	private val prefs: SharedPreferences
): RegInfoCache {
	companion object {
		const val SAVED_REG_INFO_KEY = "REG_INFO"
	}
	private val adapter = moshi.adapter(RegistrationInfo::class.java)

	override fun getRegInfo(): RegistrationInfo? {
		prefs.getString(SAVED_REG_INFO_KEY, null)?.let {
			return adapter.fromJson(it)
		} ?: return null
	}

	override fun saveRegInfo(regInfo: RegistrationInfo) {
		prefs.edit().putString(SAVED_REG_INFO_KEY, adapter.toJson(regInfo)).apply()
	}

	override fun clearRegInfo() {
		prefs.edit().remove(SAVED_REG_INFO_KEY).apply()
	}

}