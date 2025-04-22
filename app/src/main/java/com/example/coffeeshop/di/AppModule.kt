package com.example.coffeeshop.di

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.coffeeshop.data.local.AppDatabase
import com.example.coffeeshop.data.local.SharedPrefsHelper
import com.example.coffeeshop.data.local.dao.CategoryDao
import com.example.coffeeshop.data.local.dao.FavoriteDao
import com.example.coffeeshop.data.local.dao.ProductDao
import com.example.coffeeshop.data.local.dao.UserDao
import com.example.coffeeshop.data.local.entity.Category
import com.example.coffeeshop.data.local.entity.Product
import com.example.coffeeshop.data.repository.FavoriteRepository
import com.example.coffeeshop.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.*
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            AppDatabase::class.java, "coffee-db",)
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Veritabanı ilk kez oluşturulduğunda çalışır
                CoroutineScope(Dispatchers.IO).launch {
                    val database = provideAppDatabase(context)
                    prepopulateCategories(database.categoryDao())
                    prepopulateProducts(database.productDao())
                }
            }
        })
            .build()



    }

    private suspend fun prepopulateCategories(categoryDao: CategoryDao) {
        // Önce mevcut kategorileri kontrol et
        val existingCategories = categoryDao.getAllCategories()
        if (existingCategories.isEmpty()) {
            // Kategorileri oluştur
            val categories = listOf(
                Category(categoryId = 1, name = "Sıcak İçecekler"),
                Category(categoryId = 2, name = "Soğuk İçecekler"),
                Category(categoryId = 3, name = "Yiyecekler")
            )

            // Kategorileri veritabanına ekle
            categories.forEach { category ->
                categoryDao.insertCategory(category)
            }


        }
    }

    private suspend fun prepopulateProducts(productDao: ProductDao) {
        // Önce mevcut ürünleri kontrol et
        val existingProducts = productDao.getAllProducts()
        if (existingProducts.isEmpty()) {
            // ürünleri oluştur
            val products = listOf(
                Product(name = "Macchiato", price = 150.0, description = "Sıcak İçecek", categoryId = 1, imageUrl = "https://static.vecteezy.com/system/resources/thumbnails/025/282/026/small/stock-of-mix-a-cup-coffee-latte-more-motive-top-view-foodgraphy-generative-ai-photo.jpg"),
                Product(name = "Espresso", price = 100.0, description = "Sıcak İçecek", categoryId = 1, imageUrl = "https://l.icdbcdn.com/oh/53d6fac5-34ae-48dc-9783-79852170b41d.jpg?w=1040"),
                Product(name = "Latte", price = 100.0, description = "Soğuk İçecek", categoryId = 2, imageUrl = "https://img.freepik.com/premium-photo/glass-iced-latte-coffee-coffee-shop_653449-992.jpg"),
                Product(name = "Kurabiye", price = 250.0, description = "Kurabiye", categoryId = 3, imageUrl = "https://st.depositphotos.com/37930168/61608/i/450/depositphotos_616088430-stock-photo-chocolate-chip-cookies-white-bowl.jpg"),



            )

            // ürünleri veritabanına ekle
            products.forEach { product ->
                productDao.insertProduct(product)
            }
        }
    }



    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideUserRepository(userDao: UserDao): UserRepository = UserRepository(userDao)

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }
    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }
    @Provides
    fun provideFavoriteRepository(favoriteDao: FavoriteDao): FavoriteRepository = FavoriteRepository(favoriteDao)

    @Provides
    fun provideSharedPrefsHelper(@ApplicationContext context: Context): SharedPrefsHelper {
        return SharedPrefsHelper(context)
    }


}