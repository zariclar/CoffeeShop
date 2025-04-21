package com.example.coffeeshop.di

import android.content.Context
import androidx.room.*
import com.example.coffeeshop.data.local.AppDatabase
import com.example.coffeeshop.data.local.SharedPrefsHelper
import com.example.coffeeshop.data.local.dao.UserDao
import com.example.coffeeshop.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.*
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.*
import javax.inject.*

// di/AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "coffee-db"
        ).build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideUserRepository(userDao: UserDao): UserRepository = UserRepository(userDao)

    @Provides
    fun provideSharedPrefsHelper(@ApplicationContext context: Context): SharedPrefsHelper {
        return SharedPrefsHelper(context)
    }
}