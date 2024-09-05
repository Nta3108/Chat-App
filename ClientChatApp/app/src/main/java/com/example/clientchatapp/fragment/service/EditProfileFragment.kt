package com.example.clientchatapp.fragment.service

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.clientchatapp.R
import com.example.clientchatapp.databinding.FragmentEditProfileBinding
import com.example.clientchatapp.viewmodel.EditProfileViewModel
import com.example.clientchatapp.viewmodel.SharedViewModel
import com.example.serverchatapp.IChatService
import com.example.serverchatapp.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private var user: User? = null
    private var avatarUri: String = ""
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var editProfileViewModel: EditProfileViewModel

    private val pickImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val uri = data.data
                uri?.let {
                    avatarUri = it.toString()
                    takePersistableUriPermission(it)
                    binding.imageAvatar.setImageURI(it)
                }
            }
        }
    }

    private fun takePersistableUriPermission(uri: Uri) {
        requireContext().contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editProfileViewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

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

        binding.btnChangeAvatar.setOnClickListener {
            val intent =
                Intent(
                    Intent.ACTION_OPEN_DOCUMENT,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    )
                }
            pickImageResultLauncher.launch(intent)
        }

        binding.btnUpdateProfile.setOnClickListener {
            val phone = binding.editTextPhone.text.toString()
            if (phone.length != 10) {
                Toast.makeText(
                    requireContext(),
                    "Phone number must be 10 digits",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val updatedUser = user?.copy(
                nameUser = binding.editTextName.text.toString(),
                phone = phone,
                avatarUser = avatarUri.ifEmpty { user?.avatarUser ?: "" }
            )
            updatedUser?.let { updatedUser ->
                sharedViewModel.setUser(updatedUser)
                editProfileViewModel.updateProfile(updatedUser)
            }
        }

        editProfileViewModel.updateProfileState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EditProfileViewModel.UpdateProfileState.Success -> {
                    Toast.makeText(requireContext(), "Update successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }

                is EditProfileViewModel.UpdateProfileState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}