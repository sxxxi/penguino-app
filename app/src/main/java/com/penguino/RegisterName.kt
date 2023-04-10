package com.penguino

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
//import androidx.appcompat.app.AppCompatActivity
//import androidx.databinding.DataBindingUtil
//import android.widget.TextView
import androidx.navigation.findNavController
import com.penguino.databinding.FragmentBluetoothDevicesScanBinding
import com.penguino.databinding.FragmentRegisterNameBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterName.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterName : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRegisterNameBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterNameBinding.inflate(inflater, container, false)

        //initialize http variables
        val url : String = "http://10.0.2.2:8080/api/pet"
        val client: OkHttpClient = OkHttpClient()

        //onclick do request
        binding.registerBtn.setOnClickListener { view: View ->


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
            val request = Request.Builder() //GET
                .url(url) //'localhost' or ip doesnt work, have to use this because im using emulator
                .build()
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
                        response.use {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")

                            for ((name, value) in response.headers) {
                                println("$name: $value")
                            }
                        }
                    }
                })

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")

                            for ((name, value) in response.headers) {
                                println("$name: $value")
                            }
                            Log.e("result", response.body!!.string())
                        }
                    }
                })
            }).start()
        }



        return binding.root
        // inflater.inflate(R.layout.fragment_register_name, container, false)


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_register_name.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterName().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}