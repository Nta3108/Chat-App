package com.example.clientchatapp.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchatapp.R
import com.example.serverchatapp.entities.Conversation
import com.example.clientchatapp.databinding.ItemConversationBinding
import com.example.clientchatapp.utils.Constants
import com.example.clientchatapp.fragment.home.ChatListFragment

class ConversationAdapter(
    private val homeFragment: ChatListFragment,
    private val navController: NavController
) : RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(conversation: Conversation) {
            binding.textName.text = conversation.senderName
            binding.textLastMessage.text = conversation.lastMessage
            binding.textTimestamp.text = conversation.timestamp
            binding.imageConversation.setImageResource(R.drawable.img_avatar)
        }
    }

    // Original list of conversations
    private var originalConversations: List<Conversation> = emptyList()

    // Displayed list of conversations
    private var conversations: List<Conversation> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemConversationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation)

        // Handle item click
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putLong(Constants.KEY_CONVERSATION_ID, conversation.conversationID)
                putLong(Constants.KEY_SENDER_ID, conversation.receiverID)
                putLong(Constants.KEY_RECEIVER_ID, conversation.senderID)
                putString(Constants.KEY_RECEIVER_NAME, conversation.senderName)
                putString(Constants.KEY_RECEIVER_AVT, conversation.senderAvt)
            }
            navController.navigate(R.id.action_chatListFragment_to_chatScreenFragment, bundle)
        }

        // Handle long click
        holder.itemView.setOnLongClickListener {
           // homeFragment.removeConversationWithId(conversation.conversationID)
            true
        }
    }

    override fun getItemCount(): Int = conversations.size

    fun setData(newList: List<Conversation>) {
        // Set the original list only if it's currently empty (i.e., first-time setup)
        if (originalConversations.isEmpty()) {
            originalConversations = newList
        }

        val diffCallback = ConversationDiffCallback(conversations, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        conversations = newList
        diffResult.dispatchUpdatesTo(this)
    }

    // Method to get the original list of conversations
    fun getOriginalData(): List<Conversation> {
        return originalConversations
    }
}
