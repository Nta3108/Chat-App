package com.example.serverchatapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.serverchatapp.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM users")
    fun getRemoteAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE userID = :userID")
    fun getUserByID(userID: Long): LiveData<User>

    @Query("SELECT * FROM users WHERE nameUser LIKE :name")
    fun searchUserByName(name: String): LiveData<List<User>>
}
