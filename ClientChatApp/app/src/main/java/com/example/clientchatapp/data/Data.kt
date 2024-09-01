package com.example.clientchatapp.data

import android.content.Context
import android.net.Uri
import com.example.clientchatapp.R
import com.example.serverchatapp.entities.User

class Data {
    companion object{
        fun getFakeUsers(context: Context): MutableList<User> {
            return mutableListOf(
                User(
                    userID = 1L,
                    nameUser = "John Doe",
                    phone = "0123456789",
                    avatarUser = getUriFromDrawable(context, R.drawable.img_avtone).toString()
                ),
                User(
                    userID = 2L,
                    nameUser = "Jane Smith",
                    phone = "0987654321",
                    avatarUser = getUriFromDrawable(context, R.drawable.img_avttwo).toString()
                ),
                User(
                    userID = 3L,
                    nameUser = "Alice Johnson",
                    phone = "0456123789",
                    avatarUser = getUriFromDrawable(context, R.drawable.img_avtthree).toString()
                ),
                User(
                    userID = 4L,
                    nameUser = "Bob Brown",
                    phone = "0789456123",
                    avatarUser = getUriFromDrawable(context, R.drawable.img_avtfour).toString()
                )
            )
        }

        fun getUriFromDrawable(context: Context, drawableId: Int): Uri {
            return Uri.parse("android.resource://${context.packageName}/$drawableId")
        }
    }
}