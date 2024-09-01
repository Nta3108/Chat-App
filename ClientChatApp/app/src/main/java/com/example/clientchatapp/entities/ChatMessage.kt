package com.example.serverchatapp.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "chats",
    foreignKeys = [ForeignKey(
        entity = Conversation::class,
        parentColumns = ["conversationID"],
        childColumns = ["conversationID"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["conversationID"])]
)
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val chatID: Long,
    val senderID: Long,
    val receiverID: Long,
    val message: String,
    val timestamp: String,
    val conversationID: Long
) : Parcelable
