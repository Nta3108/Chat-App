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
import com.example.clientchatapp.databinding.FragmentLoginBinding
import com.example.serverchatapp.IChatService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private var chatManager: IChatService? = null
    private var isConnect = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            chatManager = IChatService.Stub.asInterface(service)
            isConnect = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            chatManager = null
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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnLogin.setOnClickListener {
            val phone = binding.editTextPhone.text.toString().trim()

            if (phone.length == 10) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val users = chatManager?.getAllUsers()?.filter { it.phone == phone }
                        withContext(Dispatchers.Main) {
                            if (users.isNullOrEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    "User not found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val user = users.first()
                                Toast.makeText(
                                    requireContext(),
                                    "Login successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val action =
                                    LoginFragmentDirections.actionLoginFragmentToHomeFragment(user)
                                findNavController().navigate(action)
                            }
                        }
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Failed to login", Toast.LENGTH_SHORT)
                                .show()
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

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

}