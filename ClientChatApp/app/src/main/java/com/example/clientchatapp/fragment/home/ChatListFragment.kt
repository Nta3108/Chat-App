package com.example.clientchatapp.fragment.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clientchatapp.R
import com.example.clientchatapp.adapter.ConversationAdapter
import com.example.clientchatapp.data.ConversationData
import com.example.clientchatapp.databinding.FragmentChatListBinding
import kotlinx.coroutines.launch

class ChatListFragment : Fragment() {

    private lateinit var binding: FragmentChatListBinding
    private lateinit var conversationAdapter: ConversationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadFakeConversations()
    }

    private fun setupUI() {
        binding.btnSearch.setOnClickListener {
            val query = binding.editTextSearch.text.toString()
            filterConversations(query)
        }

        conversationAdapter = ConversationAdapter(
            onClick = { conversation ->
                val action = ChatListFragmentDirections.actionChatListFragmentToChatScreenFragment()
                findNavController().navigate(action)
            },
            onLongClick = { conversation ->
                removeConversationWithId(conversation.conversationID)
            }
        )

        binding.recyclerViewConversation.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewConversation.adapter = conversationAdapter
    }

    private fun loadFakeConversations() {
        val fakeConversations = ConversationData.getFakeConversations()
        conversationAdapter.setData(fakeConversations)
    }

    private fun filterConversations(query: String) {
        val filteredList = conversationAdapter.getOriginalData().filter { conversation ->
            conversation.senderName?.contains(query, ignoreCase = true) == true ||
                    conversation.receiverName?.contains(query, ignoreCase = true) == true
        }
        conversationAdapter.setData(filteredList)
    }

    private fun removeConversationWithId(conversationId: Long) {
        val removeDialog = Dialog(requireContext())
        removeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        removeDialog.setContentView(R.layout.dialog_remove_conversation)

        val textRemove: TextView = removeDialog.findViewById(R.id.text_dialog_delete)
        val textCancel: TextView = removeDialog.findViewById(R.id.text_dialog_cancel)

        textRemove.setOnClickListener {
            lifecycleScope.launch {
                // Remove the conversation from the adapter
                conversationAdapter.removeConversation(conversationId)

                // Show a toast message to indicate success
                Toast.makeText(requireContext(), "Conversation removed", Toast.LENGTH_SHORT).show()

                // Dismiss the dialog
                removeDialog.dismiss()
            }
        }

        textCancel.setOnClickListener { removeDialog.dismiss() }
        removeDialog.show()
    }
}
