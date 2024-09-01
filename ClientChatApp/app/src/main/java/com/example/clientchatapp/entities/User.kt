package com.example.serverchatapp.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userID: Long,
    val nameUser: String,
    val phone: String,
    val avatarUser: String
) : Parcelable
