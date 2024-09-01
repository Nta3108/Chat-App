package com.example.clientchatapp.fragment.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.clientchatapp.R
import com.example.clientchatapp.databinding.FragmentSettingBinding
import com.example.clientchatapp.viewmodel.SharedViewModel


class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                binding.textViewName.text = user.nameUser
                binding.textViewPhone.text = user.phone

                val uri = Uri.parse(user.avatarUser)
                Glide.with(binding.imageAvatar.context)
                    .load(uri)
                    .placeholder(R.drawable.img_avatar)
                    .into(binding.imageAvatar)
            } else {
                binding.textViewName.text = "No user data"
                binding.textViewPhone.text = "No phone number"
            }
        })

        binding.btnEditProfile.setOnClickListener {
            sharedViewModel.user.value?.let { user ->
                val action = SettingFragmentDirections.actionSettingFragmentToEditProfileFragment(user)
                findNavController().navigate(action)
            }
        }

        binding.btnPolicy.setOnClickListener {
            val url = "https://purechat.com/privacy"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        }
    }
}