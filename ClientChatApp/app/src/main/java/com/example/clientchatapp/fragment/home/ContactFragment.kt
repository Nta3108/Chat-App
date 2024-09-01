package com.example.clientchatapp.fragment.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clientchatapp.adapter.ContactAdapter
import com.example.clientchatapp.databinding.FragmentContactBinding
import com.example.serverchatapp.IChatService
import com.example.serverchatapp.entities.User


class ContactFragment : Fragment() {

    private lateinit var binding: FragmentContactBinding
    private lateinit var contactAdapter: ContactAdapter
    private var chatManager: IChatService? = null
    private var isConnect = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            chatManager = IChatService.Stub.asInterface(service)
            isConnect = true
            loadContacts()
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
        binding = FragmentContactBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactAdapter = ContactAdapter()
        binding.recyclerViewContact.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContact.adapter = contactAdapter

        binding.btnSearch.setOnClickListener {
            searchContactByName()
        }
    }

    private fun loadContacts(){
        val users: List<User>? = chatManager?.allUsers
        if (users != null) {
            contactAdapter.submitList(users)
        }
    }

    private fun searchContactByName() {
        val searchName = binding.editTextSearch.text.toString().trim()
        if (searchName.isNotBlank()) {
            val users = chatManager?.searchUserByName(searchName)
            if (users != null && users.isNotEmpty()) {
                contactAdapter.submitList(users)
            } else {
                Toast.makeText(requireContext(), "No users found", Toast.LENGTH_SHORT).show()
            }
        } else {
            loadContacts()
        }
    }
}