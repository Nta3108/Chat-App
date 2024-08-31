package com.example.serverchatapp.repository

import com.example.serverchatapp.dao.ConversationDao
import com.example.serverchatapp.entities.Conversation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepository @Inject constructor(
    private val conversationDao: ConversationDao
) {

    fun insertConversation(conversation: Conversation) {
        conversationDao.insertConversation(conversation)
    }

    fun deleteConversationByID(conversationID: Long) {
        conversationDao.deleteConversationByID(conversationID)
    }

    fun getAllConversations(): List<Conversation> {
        return conversationDao.getRemoteAllConversations()
    }

    fun getConversationByID(conversationID: Long): Conversation? {
        return conversationDao.getConversationByID(conversationID).value
    }

    fun updateConversation(conversationId: Long, lastMessage: String, timestamp: String) {
        conversationDao.updateConversation(conversationId, lastMessage, timestamp)
    }
}