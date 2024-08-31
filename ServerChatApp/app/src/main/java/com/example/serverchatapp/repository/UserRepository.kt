package com.example.serverchatapp.repository

import com.example.serverchatapp.dao.UserDao
import com.example.serverchatapp.entities.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    fun getAllUsers(): List<User> {
        return userDao.getRemoteAllUsers()
    }

    fun getUserByID(userID: Long): User? {
        return userDao.getUserByID(userID).value
    }

    fun searchUserByName(name: String): List<User> {
        return userDao.searchUserByName(name).value ?: emptyList()
    }
}