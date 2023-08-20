package com.penguino.data.cache

import android.content.SharedPreferences
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.models.forms.PetRegistrationForm
import com.squareup.moshi.Moshi
import javax.inject.Inject

class RegInfoCacheImpl @Inject constructor(
	moshi: Moshi,
	private val prefs: SharedPreferences
): RegInfoCache {
	companion object {
		const val SAVED_REG_INFO_KEY = "REG_INFO"
	}
	private val adapter = moshi.adapter(PetRegistrationForm::class.java)

	override fun getRegInfo(): PetRegistrationForm? {
		prefs.getString(SAVED_REG_INFO_KEY, null)?.let {
			return adapter.fromJson(it)
		} ?: return null
	}

	override fun saveRegInfo(regInfo: PetRegistrationForm) {
		prefs.edit().putString(SAVED_REG_INFO_KEY, adapter.toJson(regInfo)).apply()
	}

	override fun clearRegInfo() {
		prefs.edit().remove(SAVED_REG_INFO_KEY).apply()
	}

}