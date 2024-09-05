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

class RegisterViewModel(application: Application) : BaseViewModel(application) {

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    fun registerUser(name: String, phone: String, avatar: String) {
        if (phone.length != 10) {
            _registerState.postValue(RegisterState.Error("Phone number must be 10 digits"))
            return
        }

        if (!isServiceConnected) {
            _registerState.postValue(RegisterState.Error("Service not connected"))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val users = chatManager?.getAllUsers() ?: emptyList()
                val doesUserExist = users.any { it.phone == phone }

                if (doesUserExist) {
                    _registerState.postValue(RegisterState.Error("Phone number already registered"))
                } else {
                    val user = User(userID = 0, nameUser = name, phone = phone, avatarUser = avatar)
                    chatManager?.insertUser(user)
                    _registerState.postValue(RegisterState.Success)
                }
            } catch (e: RemoteException) {
                _registerState.postValue(RegisterState.Error("Failed to register user: ${e.message}"))
            }
        }
    }

    sealed class RegisterState {
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}