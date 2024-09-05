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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clientchatapp.adapter.ContactAdapter
import com.example.clientchatapp.databinding.FragmentContactBinding
import com.example.clientchatapp.viewmodel.ContactViewModel
import com.example.serverchatapp.IChatService
import com.example.serverchatapp.entities.User


class ContactFragment : Fragment() {

    private lateinit var binding: FragmentContactBinding
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var contactViewModel: ContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)

        contactAdapter = ContactAdapter()
        binding.recyclerViewContact.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContact.adapter = contactAdapter

        contactViewModel.contactsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ContactViewModel.ContactsState.Success -> {
                    contactAdapter.submitList(state.users)
                }

                is ContactViewModel.ContactsState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        contactViewModel.loadContacts()

        binding.btnSearch.setOnClickListener {
            searchContactByName()
        }
    }

    private fun searchContactByName() {
        val searchName = binding.editTextSearch.text.toString().trim()
        if (searchName.isNotBlank()) {
            contactViewModel.searchContacts(searchName)
        } else {
            contactViewModel.loadContacts()
        }
    }
}