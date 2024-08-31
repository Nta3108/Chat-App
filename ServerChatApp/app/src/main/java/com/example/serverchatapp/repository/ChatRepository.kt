package com.example.serverchatapp.repository

import com.example.serverchatapp.dao.ChatDao
import com.example.serverchatapp.dao.ConversationDao
import com.example.serverchatapp.dao.UserDao
import com.example.serverchatapp.entities.ChatMessage
import com.example.serverchatapp.entities.Conversation
import com.example.serverchatapp.entities.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatDao: ChatDao
) {

    fun insertChat(chatMessage: ChatMessage) {
        chatDao.insertChat(chatMessage)
    }

    fun getChatByID(chatID: Long): ChatMessage {
        return chatDao.getChatByID(chatID)
    }

    fun deleteChatByID(chatID: Long) {
        chatDao.deleteChatByID(chatID)
    }

    fun getAllChats(): List<ChatMessage> {
        return chatDao.getRemoteAllChats()
    }

    fun getChatInConversation(conversationId: Long): List<ChatMessage> {
        return chatDao.getChatInConversation(conversationId).value ?: emptyList()
    }
}
