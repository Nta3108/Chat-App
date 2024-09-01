package com.example.clientchatapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clientchatapp.R
import com.example.clientchatapp.databinding.ItemContactBinding
import com.example.serverchatapp.entities.User

class ContactAdapter: RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var users: List<User> = emptyList()

    fun submitList(list: List<User>) {
        users = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapter.ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactAdapter.ContactViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int  = users.size

    inner class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.textViewName.text = user.nameUser

            val avatarUri: Uri = Uri.parse(user.avatarUser)
            Glide.with(binding.imageAvatar.context)
                .load(avatarUri)
                .placeholder(R.drawable.img_avatar)
                .into(binding.imageAvatar)
        }
    }

}