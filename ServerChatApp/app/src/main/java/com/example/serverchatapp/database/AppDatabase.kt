package com.example.serverchatapp.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.serverchatapp.dao.ChatDao
import com.example.serverchatapp.dao.ConversationDao
import com.example.serverchatapp.dao.UserDao
import com.example.serverchatapp.entities.ChatMessage
import com.example.serverchatapp.entities.Conversation
import com.example.serverchatapp.entities.User

@Database(entities = [User::class, ChatMessage::class, Conversation::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun conversationDao(): ConversationDao
}
