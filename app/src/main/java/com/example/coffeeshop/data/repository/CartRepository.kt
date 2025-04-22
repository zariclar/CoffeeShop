package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.local.dao.CartDao
import com.example.coffeeshop.data.local.entity.CartItem

class CartRepository(private val dao: CartDao) {
    suspend fun add(userId: Int, productId: Long, qty: Int, content: String?) =
        dao.addToCart(CartItem(userId = userId, productId = productId, quantity = qty, content = content))

    suspend fun update(item: CartItem) = dao.updateCartItem(item)
    suspend fun remove(item: CartItem) = dao.removeCartItem(item)
    suspend fun list(userId: String) = dao.getCartItems(userId)
    suspend fun get(userId: String, productId: Long) = dao.getCartItem(userId, productId)
    suspend fun clear(userId: String) = dao.clearCart(userId)
}