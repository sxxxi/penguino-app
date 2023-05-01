package com.penguino.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguino.bluetooth.models.RegistrationInfo
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "RegistrationVM"

class RegistrationVM(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    var regInfo = mutableStateOf( RegistrationInfo() )

    companion object {
        private const val SAVED_REG_INFO = "RegistrationInfo"
        private var instance: RegistrationVM? = null
        val Factory = object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                if (instance == null) {
                    instance = RegistrationVM(extras.createSavedStateHandle())
                }
                return instance as T
            }
        }
    }

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

    fun updateRegInfo(updateLambda: (RegistrationInfo) -> Unit) {
        val copy = regInfo.value.copy()
        updateLambda(copy)
        Log.d("booba" , "Old: ${regInfo.value.name}, New: ${copy.name}")
        regInfo.value = copy
    }

    fun getSuggestedNames() {

    }


}