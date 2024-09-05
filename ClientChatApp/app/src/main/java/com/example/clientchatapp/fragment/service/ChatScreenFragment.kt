package com.example.clientchatapp.fragment.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clientchatapp.R
import com.example.clientchatapp.adapter.MessageAdapter
import com.example.clientchatapp.databinding.FragmentChatScreenBinding
import com.example.serverchatapp.IChatService
import com.example.serverchatapp.entities.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ChatScreenFragment : Fragment() {

    private lateinit var binding: FragmentChatScreenBinding
    private lateinit var adapter: MessageAdapter
    private var chatService: IChatService? = null
    private var isServiceBound = false
    private var conversationId: Long = 0
    private var senderId: Long = 0
    private var receiverId: Long = 0

    // ServiceConnection for AIDL
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            chatService = IChatService.Stub.asInterface(service)
            isServiceBound = true
            loadMessages() // Load messages when connected to the service
        }

        override fun onServiceDisconnected(name: ComponentName) {
            chatService = null
            isServiceBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get data from arguments
        conversationId = requireArguments().getLong("conversation_id")
        receiverId = requireArguments().getLong("receiver_id")
        senderId = requireArguments().getLong("sender_id")

        // Set up RecyclerView
        adapter = MessageAdapter(senderId, R.drawable.img_avatar)
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewChat.adapter = adapter

        // Set up button click listeners
        binding.iconSendMessage.setOnClickListener {
            sendMessage()
        }

        // Bind to ServerChatApp service
        bindToChatService()
    }

    private fun bindToChatService() {
        val intent = Intent(IChatService::class.java.name)
        intent.action = "remote_service"
        intent.setPackage("com.example.serverchatapp")
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun loadMessages() {
        if (!isServiceBound) return
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val messages = chatService?.getChatInConversation(conversationId)
                withContext(Dispatchers.Main) {
                    adapter.setData(messages ?: emptyList())
                }
            } catch (e: Exception) {
                Log.e("ChatScreenFragment", "Error loading messages", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load messages", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun sendMessage() {
        val messageText = binding.editTextMessage.text.toString()
        if (messageText.isBlank()) {
            Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val message = ChatMessage(
            chatID = 0,
            senderID = senderId,
            receiverID = receiverId,
            message = messageText,
            timestamp = getCurrentTime(),
            conversationID = conversationId
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                chatService?.sendMessage(message)
                withContext(Dispatchers.Main) {
                    adapter.addMessage(message)
                    binding.recyclerViewChat.smoothScrollToPosition(adapter.itemCount - 1)
                }
            } catch (e: Exception) {
                Log.e("ChatScreenFragment", "Error sending message", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to send message", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.editTextMessage.text = null
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            requireContext().unbindService(serviceConnection)
            isServiceBound = false
        }
    }
}
