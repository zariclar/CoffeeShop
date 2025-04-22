package com.example.coffeeshop.data.local.dao

import androidx.room.*
import com.example.coffeeshop.data.local.entity.CartItem

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(item: CartItem): Long

    @Update
    suspend fun updateCartItem(item: CartItem)

    @Delete
    suspend fun removeCartItem(item: CartItem)

    @Query("SELECT * FROM cart WHERE userId = :userId")
    suspend fun getCartItems(userId: String): List<CartItem>

    @Query("SELECT * FROM cart WHERE userId = :userId AND productId = :productId")
    suspend fun getCartItem(userId: String, productId: Long): CartItem?

    @Query("DELETE FROM cart WHERE userId = :userId")
    suspend fun clearCart(userId: String)
}