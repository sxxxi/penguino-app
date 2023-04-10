package com.penguino.web.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.penguino.R
import com.penguino.bluetooth.models.DeviceInfo
import com.penguino.databinding.FragmentRegisterNameBinding
import com.penguino.web.NameSuggestionsRecyclerViewAdapter
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.notify
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

private const val ARG_PARAM1 = "SELECTED_DEVICE"
private const val HOST = "http://10.16.48.153:8080"
private const val SUGGESTED_NAME_NUM = 5
class RegisterNameFragment : Fragment() {

    private var selectedDevice: DeviceInfo? = null
    private lateinit var binding: FragmentRegisterNameBinding
    private val suggestedNames: ArrayList<String> = arrayListOf("Pengu", "John")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            selectedDevice = args.getSerializable(ARG_PARAM1, DeviceInfo::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterNameBinding.inflate(inflater, container, false)

        // init recyclerview
        val names = arrayListOf<String>("george", "pengu")
        val suggestedNames: RecyclerView = binding.recycleNameSuggestions
        suggestedNames.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.HORIZONTAL,
            false)
        suggestedNames.adapter = NameSuggestionsRecyclerViewAdapter(names) {
            onNameClickHandler(it)
        }
        getSuggestedNames()

        binding.registerBtn.setOnClickListener {
            registerAndConnectClickHandler()
        }

        return binding.root
    }

    private fun registerAndConnectClickHandler() {
        // Fill up all fields >:(
        val nameField = binding.addName.text
        val personalityField = binding.addPersonality.text
        val ageField = binding.addAge.text
        if (nameField.isBlank() || personalityField.isBlank() || ageField.isBlank()) {
            displayToast("Please fill up all fields.")
            return
        }

        doHttpStuff()

        // pass the device to the remote controller
        val bundle = bundleOf("SELECTED_DEVICE" to selectedDevice)
        findNavController().navigate(R.id.registerNameFragment_to_penguinoRcFragment, bundle)
    }

    private val fetchSuggestedNameCallback: Callback = object: Callback {
        override fun onFailure(call: Call, e: IOException) {

        }

        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                Log.e("ERROROROROR", "Something went wrong getting suggested names")
                return
            }

            response.body?.let { bod ->
                val nameListJson = JSONArray(bod.string())
                for (i in 0 until nameListJson.length()) {
                    suggestedNames.add(nameListJson.getString(i))
                }
                requireActivity().runOnUiThread(Runnable {
                    val adapter = NameSuggestionsRecyclerViewAdapter(suggestedNames) {
                        onNameClickHandler(it)
                    }
                    Log.d("Fooo", "trying to update list")
                    binding.recycleNameSuggestions.adapter = adapter
                    adapter?.notifyDataSetChanged()
                })
            }
        }
    }

    private fun getSuggestedNames() {
        // get names
        val url = "$HOST/api/pet/suggestNames/$SUGGESTED_NAME_NUM"
        val req = Request.Builder()
            .get()
            .url(url)
            .build()
        sendRequest(req, fetchSuggestedNameCallback)
    }

    private fun onNameClickHandler(name: String) {
        binding.addName.setText(name)
    }

    private fun doHttpStuff() {
        //initialize http variables
        val url = "$HOST/api/pet"
        val client: OkHttpClient = OkHttpClient()

        //Get user inputs
        val name = binding.addName.text.toString().trim()
        val personality = binding.addPersonality.text.toString().trim()
        val age = binding.addAge.text.toString().trim()

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

    private fun sendRequest(req: Request, callback: Callback) {
        val client = OkHttpClient()
        Thread {
            client.newCall(req).enqueue(callback)
        }.start()
    }

    private fun displayToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}