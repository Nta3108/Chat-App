package com.example.serverchatapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.serverchatapp.IChatService
import com.example.serverchatapp.entities.ChatMessage
import com.example.serverchatapp.entities.Conversation
import com.example.serverchatapp.entities.User
import com.example.serverchatapp.repository.ChatRepository
import com.example.serverchatapp.repository.ConversationRepository
import com.example.serverchatapp.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatService : Service() {

    @Inject
    lateinit var chatRepository: ChatRepository
    @Inject
    lateinit var conversationRepository: ConversationRepository
    @Inject
    lateinit var userRepository: UserRepository

    private val binder = object : IChatService.Stub() {
        //Chat function
        override fun insertChat(chatMessage: ChatMessage?) {
            CoroutineScope(Dispatchers.IO).launch {
                if (chatMessage != null) {
                    chatRepository.insertChat(chatMessage)
                }
                if (chatMessage != null) {
                    updateConversationAfterMessage(chatMessage.conversationID)
                }
            }
        }

        override fun deleteChatByID(chatID: Long) {
            CoroutineScope(Dispatchers.IO).launch {
                val chat = chatRepository.getChatByID(chatID)
                chatRepository.deleteChatByID(chatID)
                updateConversationAfterMessage(chat.conversationID)
            }
        }

        override fun getAllChats(): List<ChatMessage> {
            return chatRepository.getAllChats()
        }

        override fun getChatInConversation(conversationId: Long): List<ChatMessage> {
            return chatRepository.getChatInConversation(conversationId)
        }

        override fun sendMessage(chatMessage: ChatMessage?) {
            insertChat(chatMessage)
        }

        // Conversation function
        override fun insertConversation(conversation: Conversation?) {
            CoroutineScope(Dispatchers.IO).launch {
                if (conversation != null) {
                    conversationRepository.insertConversation(conversation)
                }
            }
        }

        override fun deleteConversationByID(conversationID: Long) {
            CoroutineScope(Dispatchers.IO).launch {
                conversationRepository.deleteConversationByID(conversationID)
            }
        }

        override fun getAllConversations(): List<Conversation> {
            return conversationRepository.getAllConversations()
        }

        override fun getConversationByID(conversationID: Long): Conversation? {
            return conversationRepository.getConversationByID(conversationID)
        }

        override fun updateConversation(
            conversationId: Long,
            lastMessage: String?,
            timestamp: String?
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                if (lastMessage != null && timestamp != null) {
                    conversationRepository.updateConversation(
                        conversationId,
                        lastMessage,
                        timestamp
                    )
                }
            }
        }

        // User function
        override fun insertUser(user: User?) {
            CoroutineScope(Dispatchers.IO).launch {
                if (user != null) {
                    userRepository.insertUser(user)
                }
            }
        }

        override fun updateUser(user: User?) {
            CoroutineScope(Dispatchers.IO).launch {
                if (user != null) {
                    userRepository.updateUser(user)
                }
            }
        }

        override fun getAllUsers(): List<User> {
            return userRepository.getAllUsers()
        }

        override fun getUserByID(userID: Long): User? {
            return userRepository.getUserByID(userID)
        }

        override fun searchUserByName(name: String?): List<User> {
            return userRepository.searchUserByName(name.toString())
        }

        private suspend fun updateConversationAfterMessage(conversationId: Long) {
            val chatsInConversation = chatRepository.getChatInConversation(conversationId)
            if (chatsInConversation.isNotEmpty()) {
                val lastMessage = chatsInConversation.last()
                conversationRepository.updateConversation(
                    conversationId,
                    lastMessage.message,
                    lastMessage.timestamp
                )
            } else {
                conversationRepository.updateConversation(conversationId, "", "")
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
}
