package com.example.clientchatapp.setuptest

import android.os.IBinder
import com.example.serverchatapp.IChatService
import com.example.serverchatapp.entities.ChatMessage
import com.example.serverchatapp.entities.Conversation
import com.example.serverchatapp.entities.User

open class FakeChatService : IChatService {

    val user = mutableListOf<User>()

    val users = mutableListOf(
        User(1, "John Doe", "1234567890", "avatar")
    )

    override fun asBinder(): IBinder {
        TODO("Not yet implemented")
    }

    override fun insertChat(chatMessage: ChatMessage?) {
        TODO("Not yet implemented")
    }

    override fun deleteChatByID(chatID: Long) {
        TODO("Not yet implemented")
    }

    override fun getAllChats(): MutableList<ChatMessage> {
        TODO("Not yet implemented")
    }

    override fun getChatInConversation(conversationId: Long): MutableList<ChatMessage> {
        TODO("Not yet implemented")
    }

    override fun sendMessage(chatMessage: ChatMessage?) {
        TODO("Not yet implemented")
    }

    override fun insertConversation(conversation: Conversation?) {
        TODO("Not yet implemented")
    }

    override fun deleteConversationByID(conversationID: Long) {
        TODO("Not yet implemented")
    }

    override fun getAllConversations(): MutableList<Conversation> {
        TODO("Not yet implemented")
    }

    override fun getConversationByID(conversationID: Long): Conversation {
        TODO("Not yet implemented")
    }

    override fun updateConversation(
        conversationId: Long,
        lastMessage: String?,
        timestamp: String?
    ) {
        TODO("Not yet implemented")
    }

    override fun insertUser(user: User?) {
        user?.let {
            // Assuming the user ID should be unique and incremented
            val newId = (users.maxOfOrNull { it.userID } ?: 0) + 1
            users.add(it.copy(userID = newId))
        }
    }

    override fun updateUser(user: User?) {
        user?.let {
            val index = users.indexOfFirst { u -> u.userID == it.userID }
            if (index != -1) {
                users[index] = it
            }
        }
    }


    override fun getAllUsers(): List<User> {
        return users
    }

    override fun getUserByID(userID: Long): User {
        TODO("Not yet implemented")
    }

    override fun searchUserByName(name: String?): MutableList<User> {
        return user.filter { it.nameUser.contains(name ?: "", ignoreCase = true) }.toMutableList()
    }
}
