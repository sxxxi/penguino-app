package com.penguino.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.cache.DeviceInfoCache
import com.penguino.data.local.models.DeviceInfo
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.models.Image
import com.penguino.data.models.forms.PetRegistrationForm
import com.penguino.data.repositories.registration.RegistrationRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
	deviceInfoCache: DeviceInfoCache,
	private val regRepo: RegistrationRepositoryImpl,
) : ViewModel() {
	private val _uiState = MutableStateFlow(
		RegistrationUiState(
			regInfo = RegistrationInfoEntity(),
			regForm = PetRegistrationForm(
				device = deviceInfoCache.getSelectedDevice() ?: DeviceInfo()
			)
		)
	)
	val uiState: StateFlow<RegistrationUiState> = _uiState

	fun onFormSubmit() {
		viewModelScope.launch {
			_uiState.update {
				it.copy(regForm = it.regForm.copy(name = it.regForm.name.replaceFirstChar { ch -> ch.uppercase() }))
			}
			regRepo.save(uiState.value.regForm)
		}
	}

	fun updateRegForm(updateLambda: (PetRegistrationForm) -> PetRegistrationForm) {
		_uiState.update { uiState ->
			updateLambda(uiState.regForm).let { regForm ->
				uiState.copy(regForm = updateLambda(regForm))
			}
		}
	}

	fun onPfpChange(pfp: Image?) {
		_uiState.update { state ->
			state.copy(regForm = state.regForm.copy(pfp = pfp))
		}
	}

	data class RegistrationUiState(
		val suggestions: List<String> = listOf(),
		val regInfo: RegistrationInfoEntity,
		val pfp: Bitmap? = null,
		val regForm: PetRegistrationForm = PetRegistrationForm()
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