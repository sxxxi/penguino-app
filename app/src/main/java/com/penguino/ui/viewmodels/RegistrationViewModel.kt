package com.penguino.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.local.RegInfoCache
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.repositories.registration.RegistrationRepositoryImpl
import com.penguino.data.network.RegistrationNetworkDataSource
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "RegistrationVM"

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    retrofit: Retrofit,
    private val moshi: Moshi,
    private val regInfoCache: RegInfoCache,
    private val regRepo: RegistrationRepositoryImpl
) : ViewModel() {
    private val petsApi = retrofit.create(RegistrationNetworkDataSource::class.java)
    var uiState by mutableStateOf(
		RegistrationUiState(
        regInfo = regInfoCache.getRegInfo() ?: RegistrationInfoEntity()
    )
	)

    init {
//        getSuggestedNames()
    }

    data class RegistrationUiState(
        val suggestions: List<String> = listOf(),
        val regInfo: RegistrationInfoEntity
    )

    fun updateRegInfo(updateLambda: (RegistrationInfoEntity) -> Unit) {
        val copy = uiState.regInfo.copy()
        updateLambda(copy)
        uiState = uiState.copy(regInfo = copy)
        regInfoCache.saveRegInfo(copy)
    }

    private fun getSuggestedNames() = viewModelScope.launch {
        petsApi.suggestNames(8).enqueue(object: Callback<List<String>> {
            override fun onResponse(
                call: Call<List<String>>,
                response: Response<List<String>>
            ) {
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        suggestions = response.body()?.toList() ?: listOf()
                    )

                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {}
        })
    }

    fun postRegInfo() = viewModelScope.launch {
        regRepo.saveDevice(device = uiState.regInfo, callback = object: Callback<String> {
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

/**
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