package com.example.clientchatapp.fragment.service

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.clientchatapp.R
import com.example.clientchatapp.databinding.FragmentEditProfileBinding
import com.example.clientchatapp.fragment.home.SettingFragmentArgs
import com.example.serverchatapp.entities.User

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }

        user = arguments?.let { EditProfileFragmentArgs.fromBundle(it).user }
        user?.let {
            binding.editTextName.setText(it.nameUser)
            binding.editTextPhone.setText(it.phone)

            val uri = Uri.parse(it.avatarUser)
            Glide.with(binding.imageAvatar.context)
                .load(uri)
                .placeholder(R.drawable.img_avatar)
                .into(binding.imageAvatar)
        }

    }
}