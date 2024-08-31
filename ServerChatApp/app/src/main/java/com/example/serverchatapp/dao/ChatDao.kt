package com.example.serverchatapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.serverchatapp.entities.ChatMessage

@Dao
interface ChatDao {
    @Insert
    fun insertChat(chatMessage: ChatMessage)

    @Query("SELECT * FROM chats WHERE chatID = :chatID")
    fun getChatByID(chatID: Long): ChatMessage

    @Query("DELETE FROM chats WHERE chatID = :chatID")
    fun deleteChatByID(chatID: Long)

    @Query("SELECT * FROM chats")
    fun getAllChats(): LiveData<List<ChatMessage>>

    @Query("SELECT * FROM chats")
    fun getRemoteAllChats(): List<ChatMessage>

    @Query("SELECT * FROM chats WHERE conversationID = :conversationId")
    fun getChatInConversation(conversationId: Long): LiveData<List<ChatMessage>>
}
