package com.example.coffeeshop.data.local.dao

import androidx.room.*
import com.example.coffeeshop.data.local.entity.Category

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories WHERE categoryId = :id")
    suspend fun getCategoryById(id: Long): Category?

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>
}