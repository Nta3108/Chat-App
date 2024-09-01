package com.example.clientchatapp.fragment.authentication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.clientchatapp.R
import com.example.clientchatapp.data.Data
import com.example.clientchatapp.databinding.FragmentRegisterBinding
import com.example.serverchatapp.IChatService
import com.example.serverchatapp.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private var chattManager: IChatService? = null
    private var isConnect = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            chattManager = IChatService.Stub.asInterface(service)
            isConnect = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            chattManager = null
            isConnect = false
        }
    }

    override fun onStart() {
        super.onStart()
        requireContext().bindService(Intent("com.example.serverchatapp.BIND_CHAT_SERVICE").apply {
            setComponent(
                ComponentName(
                    "com.example.serverchatapp",
                    "com.example.serverchatapp.service.ChatService"
                )
            )
        }, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isConnect) {
            requireContext().unbindService(serviceConnection)
            isConnect = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val phone = binding.editTextPhone.text.toString().trim()
            val avatar =
                Data.getUriFromDrawable(requireContext(), R.drawable.img_avtfour).toString()

            if (phone.length == 10) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val users = chattManager?.getAllUsers() ?: emptyList()
                        val doesUserExist = users.any { it.phone == phone }

                        withContext(Dispatchers.Main) {
                            if (doesUserExist) {
                                Toast.makeText(
                                    requireContext(),
                                    "Phone number already registered",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val user = User(
                                    userID = 0,
                                    nameUser = name,
                                    phone = phone,
                                    avatarUser = avatar
                                )
                                chattManager?.insertUser(user)
                                Toast.makeText(
                                    requireContext(),
                                    "Register successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            }
                        }
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Failed to register user",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Phone number must be 10 digits",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}