package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.local.dao.ProductDao
import com.example.coffeeshop.data.local.entity.Product

class ProductRepository(private val dao: ProductDao) {
    suspend fun add(product: Product) = dao.insertProduct(product)
    suspend fun update(product: Product) = dao.updateProduct(product)
    suspend fun delete(product: Product) = dao.deleteProduct(product)
    suspend fun getById(id: Long) = dao.getProductById(id)
    suspend fun listAll() = dao.getAllProducts()
    suspend fun listByCategory(categoryId: Long) = dao.getProductsByCategory(categoryId)
}