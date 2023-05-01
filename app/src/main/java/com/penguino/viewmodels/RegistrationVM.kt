package com.penguino.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.penguino.bluetooth.models.RegistrationInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class RegistrationVM: ViewModel() {
    var regInfo = mutableStateOf(RegistrationInfo())

    fun updateRegInfo(updateLambda: (RegistrationInfo) -> Unit) {
        val copy = regInfo.value.copy()
        updateLambda(copy)
        Log.d("booba" , "Old: ${regInfo.value.name}, New: ${copy.name}")
        regInfo.value = copy
    }

    fun getSuggestedNames() {

    }


}