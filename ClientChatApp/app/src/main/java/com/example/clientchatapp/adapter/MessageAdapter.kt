package com.example.clientchatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.serverchatapp.entities.ChatMessage
import com.example.clientchatapp.databinding.ItemReceivedMessageBinding
import com.example.clientchatapp.databinding.ItemSentMessageBinding
import com.example.clientchatapp.utils.Constants

class MessageAdapter(
    private val senderId: Long,
    private val receiverAvatar: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class SentViewHolder(private val sentBinding: ItemSentMessageBinding) :
        RecyclerView.ViewHolder(sentBinding.root) {
        fun bind(chatMessage: ChatMessage) {
            sentBinding.message = chatMessage
            // Additional UI logic for sent messages
        }
    }

    inner class ReceivedViewHolder(private val receivedBinding: ItemReceivedMessageBinding) :
        RecyclerView.ViewHolder(receivedBinding.root) {
        fun bind(chatMessage: ChatMessage) {
            receivedBinding.message = chatMessage
            receivedBinding.imageUser.setImageResource(receiverAvatar)
            // Additional UI logic for received messages
        }
    }

    // Mutable list to hold chat messages
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constants.VIEW_TYPE_SENT) {
            SentViewHolder(
                ItemSentMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ReceivedViewHolder(
                ItemReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderID == senderId) {
            Constants.VIEW_TYPE_SENT
        } else {
            Constants.VIEW_TYPE_RECEIVED
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == Constants.VIEW_TYPE_SENT) {
            (holder as SentViewHolder).bind(messages[position])
        } else {
            (holder as ReceivedViewHolder).bind(messages[position])
        }
    }

    // Set new data and notify changes with DiffUtil
    fun setData(newList: List<ChatMessage>) {
        val diffCallback = MessageDiffCallback(messages, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        messages.clear()
        messages.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    // Add a single message and notify the adapter
    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
