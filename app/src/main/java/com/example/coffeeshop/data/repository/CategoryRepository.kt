package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.local.dao.CategoryDao
import com.example.coffeeshop.data.local.entity.Category
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val dao: CategoryDao) {
    suspend fun add(category: Category) = dao.insertCategory(category)
    suspend fun update(category: Category) = dao.updateCategory(category)
    suspend fun delete(category: Category) = dao.deleteCategory(category)
    suspend fun getById(id: Long) = dao.getCategoryById(id)
    suspend fun listAll() = dao.getAllCategories()
}