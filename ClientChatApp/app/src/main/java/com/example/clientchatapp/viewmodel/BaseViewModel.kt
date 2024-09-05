package com.example.clientchatapp.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import com.example.serverchatapp.IChatService

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    var chatManager: IChatService? = null
    var isServiceConnected = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            chatManager = IChatService.Stub.asInterface(service)
            isServiceConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            chatManager = null
            isServiceConnected = false
        }
    }

    fun bindService() {
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

    fun unbindService() {
        if (isServiceConnected) {
            getApplication<Application>().unbindService(serviceConnection)
            isServiceConnected = false
        }
    }
}