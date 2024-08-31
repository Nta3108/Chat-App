package com.example.serverchatapp.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey(autoGenerate = true) val conversationID: Long,
    val senderID: Long,
    val senderAvt: String,
    val senderName: String,
    val receiverID: Long,
    val receiverAvt: String,
    val receiverName: String,
    val lastMessage: String,
    val timestamp: String
) : Parcelable
