package com.example.serverchatapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.serverchatapp.entities.Conversation

@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConversation(conversation: Conversation)

    @Query("DELETE FROM conversations WHERE conversationID = :conversationID")
    fun deleteConversationByID(conversationID: Long)

    @Query("SELECT * FROM conversations")
    fun getAllConversations(): LiveData<List<Conversation>>

    @Query("SELECT * FROM conversations")
    fun getRemoteAllConversations(): List<Conversation>

    @Query("SELECT * FROM conversations WHERE conversationID = :conversationID")
    fun getConversationByID(conversationID: Long): LiveData<Conversation>

    @Query("UPDATE conversations " +
            "SET lastMessage = :lastMessage, timestamp =:timestamp " +
            "WHERE conversationID = :conversationId")
    fun updateConversation(conversationId: Long, lastMessage: String, timestamp: String)
}
