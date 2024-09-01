package com.example.clientchatapp.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clientchatapp.R
import com.example.clientchatapp.databinding.ActivityMainBinding
import com.example.serverchatapp.IChatService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var chattManager: IChatService? = null
    private var isConnect = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("TAG", "onServiceConnected:$name, $service ")
            chattManager = IChatService.Stub.asInterface(service)
            isConnect = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            chattManager = null
            isConnect = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindService(Intent("com.example.serverchatapp.BIND_CHAT_SERVICE").apply {
            setComponent(
                ComponentName(
                    "com.example.serverchatapp",
                    "com.example.serverchatapp.service.ChatService"
                )
            )
        }, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isConnect) {
            unbindService(serviceConnection)
            isConnect = false
        }
    }
}