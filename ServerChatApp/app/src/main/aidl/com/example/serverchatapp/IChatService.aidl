package com.example.serverchatapp;

import com.example.serverchatapp.entities.Conversation;
import com.example.serverchatapp.entities.User;
import com.example.serverchatapp.entities.ChatMessage;

interface IChatService {

    //chat function
    void insertChat(in ChatMessage chatMessage);
    void deleteChatByID(in long chatID);
    List<ChatMessage> getAllChats();
    List<ChatMessage> getChatInConversation(long conversationId);
    void sendMessage(in ChatMessage chatMessage);

    //conversaton function
    void insertConversation(in Conversation conversation);
    void deleteConversationByID(in long conversationID);
    List<Conversation> getAllConversations();
    Conversation getConversationByID(long conversationID);
    void updateConversation(in long conversationId, in String lastMessage , in String timestamp);

    //user function
    void insertUser(in User user);
    void updateUser(in User user);
    List<User> getAllUsers();
    User getUserByID(long userID);
    List<User> searchUserByName(String name);

}
