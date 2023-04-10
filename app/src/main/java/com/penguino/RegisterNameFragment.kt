package com.penguino

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
//import androidx.appcompat.app.AppCompatActivity
//import androidx.databinding.DataBindingUtil
//import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.penguino.bluetooth.models.DeviceInfo
import com.penguino.databinding.FragmentRegisterNameBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "SELECTED_DEVICE"
private const val HOST = "http://10.16.48.153:8080"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterNameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterNameFragment : Fragment() {
    private var selectedDevice: DeviceInfo? = null
    private lateinit var binding: FragmentRegisterNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            selectedDevice = args.getSerializable(ARG_PARAM1 , DeviceInfo::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterNameBinding.inflate(inflater, container, false)

        binding.registerBtn.setOnClickListener {
            registerAndConnectClickHandler(it)
        }

        return binding.root
    }

    private fun registerAndConnectClickHandler(view: View) {
        doHttpStuff()

        // pass the device to the remote controller
        val bundle = bundleOf("SELECTED_DEVICE" to selectedDevice)
        findNavController().navigate(R.id.registerNameFragment_to_penguinoRcFragment, bundle)
    }

    private fun doHttpStuff() {
        //initialize http variables
        val url = "$HOST/api/pet"
        val client: OkHttpClient = OkHttpClient()

        //Get user inputs
        val name = binding.addName.text.toString()
        val personality = binding.addPersonality.text.toString()
        val age = binding.addAge.text.toString()

        //make json object
        val jsonObject = JSONObject()
        jsonObject.put("name", name)
        jsonObject.put("personality", personality)
        jsonObject.put("age", age)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        //initialize http requests
//            val request = Request.Builder() //GET
//                .url(url) //'localhost' or ip doesn't work, have to use this because im using emulator
//                .build()
        val requestPost = Request.Builder() //POST
            .post(requestBody)
            .url(url)
            .build()

        Thread(Runnable {
            client.newCall(requestPost).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { res ->

                        if (!res.isSuccessful) throw IOException("Unexpected code $response")

                        for ((name, value) in response.headers) {
                            Log.d("FOO", "$name: $value")
                        }
                    }
                }
            })

//                client.newCall(request).enqueue(object : Callback {
//                    override fun onFailure(call: Call, e: IOException) {
//                        e.printStackTrace()
//                    }
//
//                    override fun onResponse(call: Call, response: Response) {
//                        response.use {
//                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//                            for ((name, value) in response.headers) {
//                                println("$name: $value")
//                            }
//                            Log.e("result", response.body!!.string())
//                        }
//                    }
//                })
        }).start()
    }

}