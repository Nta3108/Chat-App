package com.example.clientchatapp.viewmodel

import android.app.Application
import android.os.RemoteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.serverchatapp.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileViewModel(application: Application) : BaseViewModel(application) {

    private val _updateProfileState = MutableLiveData<UpdateProfileState>()
    val updateProfileState: LiveData<UpdateProfileState> = _updateProfileState

    init {
        bindService()
    }

    fun updateProfile(user: User) {
        if (!isServiceConnected) {
            _updateProfileState.postValue(UpdateProfileState.Error("Service not connected"))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                chatManager?.updateUser(user)
                withContext(Dispatchers.Main) {
                    _updateProfileState.postValue(UpdateProfileState.Success(user))
                }
            } catch (e: RemoteException) {
                _updateProfileState.postValue(UpdateProfileState.Error("Failed to update profile: ${e.message}"))
            }
        }
    }

    sealed class UpdateProfileState {
        data class Success(val user: User) : UpdateProfileState()
        data class Error(val message: String) : UpdateProfileState()
    }

    override fun onCleared() {
        super.onCleared()
        unbindService()
    }
}