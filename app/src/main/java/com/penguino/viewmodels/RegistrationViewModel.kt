package com.penguino.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.penguino.cache.RegInfoCache
import com.penguino.models.RegistrationInfo
import com.penguino.repositories.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import javax.inject.Inject

private const val TAG = "RegistrationVM"

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val regInfoCache: RegInfoCache
) : ViewModel() {
    var regInfo by mutableStateOf( regInfoCache.getRegInfo() ?: RegistrationInfo() )

    fun updateRegInfo(updateLambda: (RegistrationInfo) -> Unit) {
        val copy = regInfo.copy()
        updateLambda(copy)
        regInfo = copy
        regInfoCache.saveRegInfo(copy)
    }

    val names = mutableStateListOf<String>()
    private val nameSuggestionCallback = object: Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("FOO", "Something went wrong")
            return
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.let { body ->
                val x = JSONArray(body.string())
                for (i in 0 until x.length()) {
                    names.add(x[i] as String)
                }
            }
        }
    }
    fun getSuggestedNames(): SnapshotStateList<String> {
        RegistrationRepository.getSuggestedNames(nameSuggestionCallback)
        return names
    }

    fun postRegInfo(onSuccess: () -> Unit, onFail: () -> Unit) {
        val postCallback = object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFail()
            }

            override fun onResponse(call: Call, response: Response) {
                Handler(Looper.getMainLooper())
                    .post(Runnable {
                        when(response.code) {
                            200 -> {
                                onSuccess()
                                regInfoCache.clearRegInfo()
                            }
                            else -> onFail()
                        }
                    })
            }
        }
        RegistrationRepository.postRegistrationInfo(regInfo, postCallback)
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