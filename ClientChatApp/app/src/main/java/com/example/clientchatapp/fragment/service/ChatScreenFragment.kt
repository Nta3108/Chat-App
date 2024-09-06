package com.example.clientchatapp.fragment.service

import android.app.Dialog
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clientchatapp.R
import com.example.clientchatapp.adapter.MessageAdapter
import com.example.clientchatapp.data.ConversationData
import com.example.clientchatapp.databinding.FragmentChatScreenBinding
import com.example.clientchatapp.utils.Constants
import com.example.serverchatapp.IChatService
import com.example.serverchatapp.entities.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChatScreenFragment : Fragment() {

    private lateinit var binding: FragmentChatScreenBinding
    private lateinit var adapter: MessageAdapter

    // Instance variables
    private var conversationId: Long? = null
    private var receiverId: Long = 0
    private var senderId: Long = 0
    private var receiverAvt: Int = 0
    private var receiverName: String = ""

    // AIDL remote service
    private var isServiceBound = false
    private var chatService: IChatService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initConnection()
    }

    private fun initConnection() {
        // Simulating service binding
        // You can remove or modify this section if you no longer want to use the real service
        val intent = Intent(IChatService::class.java.name)
        intent.action = "chat_service"
        intent.setPackage("com.example.serverchatapp")
        requireContext().bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Log.d(Constants.TAG, "Service Connected")
            chatService = IChatService.Stub.asInterface(iBinder)
            isServiceBound = true
            loadChat() // Fetch chat once service is connected
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(Constants.TAG, "Service Disconnected")
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

        binding.apply {
            var lifecycleOwner = viewLifecycleOwner

            // Get selected user data from arguments
            receiverId = requireArguments().getLong(Constants.KEY_RECEIVER_ID)
            senderId = requireArguments().getLong(Constants.KEY_SENDER_ID)
            conversationId = requireArguments().getLong(Constants.KEY_CONVERSATION_ID)
            receiverAvt = requireArguments().getInt(Constants.KEY_RECEIVER_AVT)
            receiverName = requireArguments().getString(Constants.KEY_RECEIVER_NAME).toString()

            // Initialize adapter and setup RecyclerView
            adapter = MessageAdapter(senderId, receiverAvt)
            recyclerViewChat.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewChat.adapter = adapter

            // Load receiver details (name and avatar)
            loadReceiverDetail(receiverName, receiverAvt)

            // Set click listeners for UI elements
            iconBack.setOnClickListener { backToHome() }
            iconSendMessage.setOnClickListener { sendMessage() }
            iconDelete.setOnClickListener { removeConversation() }
        }

        // Load fake data after setting up the view
        loadFakeData()
    }

    // Simulate loading chat messages from fake data instead of real service
    private fun loadFakeData() {
        // Fetch fake conversations
        val fakeConversations = ConversationData.getFakeConversations()

        // Find the conversation with the given conversationId (provided via arguments)
        val conversation = fakeConversations.find { it.conversationID == conversationId }
        if (conversation != null) {
            val fakeMessages = listOf(
                ChatMessage(
                    senderID = conversation.senderID,
                    receiverID = conversation.receiverID,
                    message = conversation.lastMessage,
                    chatID = 0,
                    timestamp = conversation.timestamp,
                    conversationID = conversation.conversationID
                ),
                ChatMessage(
                    senderID = conversation.receiverID,
                    receiverID = conversation.senderID,
                    message = "Sure, I'm doing great!",
                    chatID = 1,
                    timestamp = "2024-09-04 10:05 AM",
                    conversationID = conversation.conversationID
                )
                // Add more fake messages as needed
            )

            // Update the RecyclerView adapter with fake data
            adapter.setData(fakeMessages)
            binding.recyclerViewChat.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }

    private fun loadChat() {
        // Fetch chat messages from the real service
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val chatMessages = chatService?.getChatInConversation(conversationId!!)
            withContext(Dispatchers.Main) {
                if (chatMessages != null) {
                    adapter.setData(chatMessages)
                    binding.recyclerViewChat.smoothScrollToPosition(adapter.itemCount - 1)
                }
            }
        }
    }

    private fun loadReceiverDetail(receiverName: String, receiverImage: Int) {
        binding.textViewNameReceiver.text = receiverName
        binding.imageAvatarReceiver.setImageResource(receiverImage)
    }

    private fun sendMessage() {
        val messageText = binding.editTextMessage.text.toString()
        if (messageText.isBlank()) {
            Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val message = ChatMessage(
            senderID = senderId,
            receiverID = receiverId,
            message = messageText,
            chatID = 0,
            timestamp = getCurrentTime(),
            conversationID = conversationId!!
        )

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            // Here you could replace this with fake message handling
            withContext(Dispatchers.Main) {
                adapter.addMessage(message)
                binding.recyclerViewChat.smoothScrollToPosition(adapter.itemCount - 1)
            }
        }
        binding.editTextMessage.text = null
    }

    private fun removeConversation() {
        val removeDialog = Dialog(requireContext())
        removeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        removeDialog.setContentView(R.layout.dialog_remove_conversation)

        val textRemove: TextView = removeDialog.findViewById(R.id.text_dialog_delete)
        val textCancel: TextView = removeDialog.findViewById(R.id.text_dialog_cancel)
        textRemove.setOnClickListener {
            // You can remove the conversation from fake data here, or implement similar logic
            Toast.makeText(requireContext(), "Conversation removed.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            removeDialog.dismiss()
        }
        textCancel.setOnClickListener { removeDialog.dismiss() }
        removeDialog.show()
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun backToHome() {
        findNavController().popBackStack()
    }

    override fun onResume() {
        super.onResume()
        if (!isServiceBound) {
            initConnection()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isServiceBound) {
            initConnection()
        }
        loadFakeData() // Load fake data when the fragment starts
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            requireContext().unbindService(serviceConnection)
        }
    }
}
