package com.example.coffeeshop.data.local

import androidx.room.*
import com.example.coffeeshop.data.local.dao.UserDao
import com.example.coffeeshop.data.local.entity.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}