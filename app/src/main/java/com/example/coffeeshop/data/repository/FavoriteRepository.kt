package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.local.dao.FavoriteDao
import com.example.coffeeshop.data.local.entity.Favorite

class FavoriteRepository(private val dao: FavoriteDao) {
    suspend fun add(userId: String, productId: Long) =
        dao.addFavorite(Favorite(userId = userId, productId = productId))

    suspend fun remove(favorite: Favorite) = dao.removeFavorite(favorite)
    suspend fun list(userId: String) = dao.getFavoritesForUser(userId)
    suspend fun isFav(userId: String, productId: Long) = dao.isFavorite(userId, productId)
    suspend fun clear(userId: String) = dao.clearFavorites(userId)
}