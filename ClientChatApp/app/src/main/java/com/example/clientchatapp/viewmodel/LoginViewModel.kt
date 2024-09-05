package com.example.clientchatapp.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.serverchatapp.IChatService
import com.example.serverchatapp.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : BaseViewModel(application) {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun login(phone: String) {
        if (phone.length != 10) {
            _loginState.postValue(LoginState.Error("Phone number must be 10 digits"))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val users = chatManager?.getAllUsers()?.filter { it.phone == phone }
                if (users.isNullOrEmpty()) {
                    _loginState.postValue(LoginState.Error("User not found"))
                } else {
                    val user = users.first()
                    _loginState.postValue(LoginState.Success(user))
                }
            } catch (e: RemoteException) {
                _loginState.postValue(LoginState.Error("Failed to login: ${e.message}"))
            }
        }
    }

    sealed class LoginState {
        data class Success(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}