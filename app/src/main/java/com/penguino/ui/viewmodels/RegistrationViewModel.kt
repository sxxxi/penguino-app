package com.penguino.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.cache.RegInfoCache
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.network.RegistrationNetworkDataSource
import com.penguino.data.repositories.registration.RegistrationRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
	retrofit: Retrofit,
	private val regInfoCache: RegInfoCache,
	private val regRepo: RegistrationRepositoryImpl,
) : ViewModel() {
	private val petsApi = retrofit.create(RegistrationNetworkDataSource::class.java)
	private val _uiState = MutableStateFlow(
		RegistrationUiState(
			regInfo = regInfoCache.getRegInfo() ?: RegistrationInfoEntity()
		)
	)
	val uiState: StateFlow<RegistrationUiState> = _uiState

	fun updateRegInfo(updateLambda: (RegistrationInfoEntity) -> RegistrationInfoEntity) {
		_uiState.update { uiState ->
			updateLambda(uiState.regInfo).let { regInfo ->
				regInfoCache.saveRegInfo(regInfo)
				uiState.copy(regInfo = updateLambda(regInfo))
			}
		}
	}

	private fun getSuggestedNames() = viewModelScope.launch {
		petsApi.suggestNames(8).enqueue(object : Callback<List<String>> {
			override fun onResponse(
				call: Call<List<String>>,
				response: Response<List<String>>
			) {
				if (response.isSuccessful) {
					_uiState.update { uiState ->
						uiState.copy(suggestions = response.body()?.toList() ?: listOf())
					}
				}
			}

			override fun onFailure(call: Call<List<String>>, t: Throwable) {}
		})
	}

	fun postRegInfo() = viewModelScope.launch {
		// Sanitize inputs
		updateRegInfo { state ->
			state.copy(
				petName = state.petName.trim().replaceFirstChar { it.uppercase() }
			)
		}

		regRepo.saveDevice(device = uiState.value.regInfo, callback = object : Callback<String> {
			override fun onResponse(call: Call<String>, response: Response<String>) {
				if (response.isSuccessful) {
					Log.d("FOO", response.body() ?: "Ok")
					regInfoCache.clearRegInfo()
				}
			}

			override fun onFailure(call: Call<String>, t: Throwable) {}
		})
		regInfoCache.clearRegInfo()
	}

	data class RegistrationUiState(
		val suggestions: List<String> = listOf(),
		val regInfo: RegistrationInfoEntity
	)
}


// REFERENCE
//    companion object {
//        private var instance: RegistrationVM? = null
//        val Factory = object: ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
//                if (instance == null) {
//                    instance = RegistrationVM(extras.createSavedStateHandle())
//                }
//                return instance as T
//            }
//        }
//    }

/*
 * Please don't use: _mutTest, test, and testUpdate.
 * This is only for experimenting with SavedStateHandle and
 * will be removed in the future.
 */
//    private var _mutTest = MutableStateFlow<RegistrationInfo>(
//        savedStateHandle.getStateFlow(
//            SAVED_REG_INFO, RegistrationInfo()).value)
//    val test = _mutTest.asStateFlow()
//
//    fun testUpdate(updateLambda: (RegistrationInfo) -> Unit) {
//        updateLambda(_mutTest.value)
//        Log.d(TAG, "Check: ${Json.encodeToString(test.value)}")
//    }