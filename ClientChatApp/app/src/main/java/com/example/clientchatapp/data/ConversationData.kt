package com.example.clientchatapp.data

import com.example.serverchatapp.entities.Conversation

object ConversationData {

    fun getFakeConversations(): List<Conversation> {
        return listOf(
            Conversation(
                conversationID = 1L,
                senderID = 101L,
                receiverID = 202L,
                senderName = "Alice",
                senderAvt = "https://example.com/avatar/alice.jpg",
                receiverName = "Bob",
                receiverAvt = "https://example.com/avatar/bob.jpg",
                lastMessage = "Hey, how's it going?",
                timestamp = "2024-09-04 10:00"
            ),
            Conversation(
                conversationID = 2L,
                senderID = 102L,
                receiverID = 203L,
                senderName = "Charlie",
                senderAvt = "https://example.com/avatar/charlie.jpg",
                receiverName = "Dave",
                receiverAvt = "https://example.com/avatar/dave.jpg",
                lastMessage = "Let's meet up tomorrow.",
                timestamp = "2024-09-04 09:30"
            ),
            Conversation(
                conversationID = 3L,
                senderID = 103L,
                receiverID = 201L,
                senderName = "Eve",
                senderAvt = "https://example.com/avatar/eve.jpg",
                receiverName = "Alice",
                receiverAvt = "https://example.com/avatar/alice.jpg",
                lastMessage = "Can you send me the report?",
                timestamp = "2024-09-04 11:15"
            )
        )
    }
}
