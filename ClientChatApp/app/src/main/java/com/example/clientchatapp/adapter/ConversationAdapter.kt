package com.example.clientchatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchatapp.R
import com.example.clientchatapp.databinding.ItemConversationBinding
import com.example.serverchatapp.entities.Conversation

class ConversationAdapter(
    private val onClick: (Conversation) -> Unit,
    private val onLongClick: (Conversation) -> Unit // Add this to handle long-click for deletion
) : ListAdapter<Conversation, ConversationAdapter.ConversationViewHolder>(ConversationDiffCallback()) {

    private var originalList: List<Conversation> = listOf() // Original list for filtering
    private var filteredList: List<Conversation> = listOf() // Filtered list for the RecyclerView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding =
            ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = getItem(position)
        holder.bind(conversation)
    }

    // Method to filter the conversations based on the search query
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.senderName?.contains(query, ignoreCase = true) == true ||
                        it.receiverName?.contains(query, ignoreCase = true) == true
            }
        }
        submitList(filteredList)
    }

    // Method to set the new list of conversations
    fun setData(data: List<Conversation>) {
        originalList = data
        filteredList = data
        submitList(data) // Refresh the RecyclerView with the new data
    }

    // Method to get the original list of conversations (before any filtering)
    fun getOriginalData(): List<Conversation> = originalList

    // Method to remove a conversation by ID
    fun removeConversation(conversationId: Long) {
        originalList = originalList.filter { it.conversationID != conversationId }
        filteredList = filteredList.filter { it.conversationID != conversationId }
        submitList(filteredList) // Update the adapter with the filtered list
    }

    inner class ConversationViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(conversation: Conversation) {
            with(binding) {
                textName.text = conversation.senderName ?: ""
                textLastMessage.text = conversation.lastMessage ?: ""
                textTimestamp.text = conversation.timestamp ?: ""

                // Load avatar image (placeholder for now)
                imageConversation.setImageResource(R.drawable.img_avatar)

                // Set click and long-click listeners
                root.setOnClickListener { onClick(conversation) }
                root.setOnLongClickListener {
                    onLongClick(conversation)
                    true
                }
            }
        }
    }

    class ConversationDiffCallback : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.conversationID == newItem.conversationID
        }

        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem == newItem
        }
    }
}
