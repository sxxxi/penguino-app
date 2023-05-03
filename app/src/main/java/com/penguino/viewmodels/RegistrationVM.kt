package com.penguino.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.penguino.bluetooth.models.RegistrationInfo
import com.penguino.utils.http.RegistrationRepository
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

private const val TAG = "RegistrationVM"

class RegistrationVM(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    var regInfo = mutableStateOf( RegistrationInfo() )

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

    fun updateRegInfo(updateLambda: (RegistrationInfo) -> Unit) {
        val copy = regInfo.value.copy()
        updateLambda(copy)
        regInfo.value = copy
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
                            200 -> onSuccess()
                            else -> onFail()
                        }
                    })
            }
        }


        RegistrationRepository.postRegistrationInfo(regInfo.value, postCallback)
    }


}