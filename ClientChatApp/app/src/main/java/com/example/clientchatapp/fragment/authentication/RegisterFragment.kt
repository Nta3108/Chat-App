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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.clientchatapp.R
import com.example.clientchatapp.data.Data
import com.example.clientchatapp.databinding.FragmentRegisterBinding
import com.example.clientchatapp.viewmodel.RegisterViewModel
import com.example.serverchatapp.IChatService
import com.example.serverchatapp.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        registerViewModel.bindService()

        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val phone = binding.editTextPhone.text.toString().trim()
            val avatar =
                Data.getUriFromDrawable(requireContext(), R.drawable.img_avttwo).toString()

            registerViewModel.registerUser(name, phone, avatar)
        }

        registerViewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegisterViewModel.RegisterState.Success -> {
                    Toast.makeText(requireContext(), "Register successful", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }

                is RegisterViewModel.RegisterState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        registerViewModel.unbindService()
    }
}