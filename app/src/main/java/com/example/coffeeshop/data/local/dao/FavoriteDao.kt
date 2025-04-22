package com.example.coffeeshop.data.local.dao

import androidx.room.*
import com.example.coffeeshop.data.local.entity.Favorite

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: Favorite): Long

    @Delete
    suspend fun removeFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorites WHERE userId = :userId")
    suspend fun getFavoritesForUser(userId: String): List<Favorite>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND productId = :productId)")
    suspend fun isFavorite(userId: String, productId: Long): Boolean

    @Query("DELETE FROM favorites WHERE userId = :userId")
    suspend fun clearFavorites(userId: String)
}