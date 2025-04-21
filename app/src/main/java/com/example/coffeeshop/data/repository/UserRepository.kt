package com.example.coffeeshop.data.repository

import android.content.Context
import com.example.coffeeshop.MyApplication
import com.example.coffeeshop.data.local.SharedPrefsHelper
import com.example.coffeeshop.data.local.dao.UserDao
import com.example.coffeeshop.data.local.entity.User

// data/repository/UserRepository.kt
class UserRepository(private val userDao: UserDao) {
    suspend fun login(email: String, password: String): User? {
        return userDao.getUserByCredentials(email, password)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun register(user: User): Boolean {
        return try {
            // Email kontrolü (Zaten kayıtlı mı?)
            val existingUser = userDao.getUserByEmail(user.userId)
            if (existingUser != null) {
                throw Exception("Bu email zaten kayıtlı!")
            }
            userDao.insertUser(user)
            val sharedPrefs = SharedPrefsHelper(MyApplication.getContext())
            sharedPrefs.saveUserId(user.userId) // Otomatik giriş yap
            true
        } catch (e: Exception) {
            false
        }
    }

}