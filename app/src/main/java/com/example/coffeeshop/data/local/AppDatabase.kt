package com.example.coffeeshop.data.local

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.coffeeshop.data.local.dao.CartDao
import com.example.coffeeshop.data.local.dao.CategoryDao
import com.example.coffeeshop.data.local.dao.FavoriteDao
import com.example.coffeeshop.data.local.dao.ProductDao
import com.example.coffeeshop.data.local.dao.UserDao
import com.example.coffeeshop.data.local.entity.CartItem
import com.example.coffeeshop.data.local.entity.Category
import com.example.coffeeshop.data.local.entity.Favorite
import com.example.coffeeshop.data.local.entity.Product
import com.example.coffeeshop.data.local.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [
    User::class,
    Category::class,
    Product::class,
    Favorite::class,
    CartItem::class
    // Diğer tablolar buraya eklenebilir
                     ], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun cartDao(): CartDao


}