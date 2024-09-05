package com.example.clientchatapp.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.serverchatapp.IChatService
import com.example.serverchatapp.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val _contactsState = MutableLiveData<ContactsState>()
    val contactsState: LiveData<ContactsState> = _contactsState

    var chatManager: IChatService? = null
    var isServiceConnected = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            chatManager = IChatService.Stub.asInterface(service)
            isServiceConnected = true
            loadContacts()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            chatManager = null
            isServiceConnected = false
        }
    }

    init {
        bindService()
    }

    private fun bindService() {
        val intent = Intent("com.example.serverchatapp.BIND_CHAT_SERVICE").apply {
            setComponent(
                ComponentName(
                    "com.example.serverchatapp",
                    "com.example.serverchatapp.service.ChatService"
                )
            )
        }
        getApplication<Application>().bindService(
            intent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun unbindService() {
        if (isServiceConnected) {
            getApplication<Application>().unbindService(serviceConnection)
            isServiceConnected = false
        }
    }

    private fun ensureServiceConnected(action: () -> Unit) {
        if (isServiceConnected) {
            action()
        }
    }

    fun loadContacts() {
        ensureServiceConnected {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val users = chatManager?.getAllUsers()
                    withContext(Dispatchers.Main) {
                        if (users != null) {
                            _contactsState.postValue(ContactsState.Success(users))
                        } else {
                            _contactsState.postValue(ContactsState.Error("No contacts found"))
                        }
                    }
                } catch (e: RemoteException) {
                    _contactsState.postValue(ContactsState.Error("Failed to load contacts: ${e.message}"))
                }
            }
        }
    }

    fun searchContacts(name: String) {
        ensureServiceConnected {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val users = chatManager?.searchUserByName(name) ?: emptyList()
                    withContext(Dispatchers.Main) {
                        if (users.isNotEmpty()) {
                            _contactsState.postValue(ContactsState.Success(users))
                        } else {
                            _contactsState.postValue(ContactsState.Error("No users found"))
                        }
                    }
                } catch (e: RemoteException) {
                    _contactsState.postValue(ContactsState.Error("Failed to search contacts: ${e.message}"))
                }
            }
        }
    }

    sealed class ContactsState {
        data class Success(val users: List<User>) : ContactsState()
        data class Error(val message: String) : ContactsState()
    }

    override fun onCleared() {
        super.onCleared()
        unbindService()
    }
}