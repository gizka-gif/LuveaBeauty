package com.example.luveabeauty.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    // READ: Menampilkan semua produk favorit
    @Query("SELECT * FROM favorites ORDER BY id DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    // READ: Memeriksa apakah produk sudah menjadi favorit
    @Query("SELECT * FROM favorites WHERE productId = :productId LIMIT 1")
    suspend fun getFavorite(productId: Int): FavoriteEntity?

    // CREATE: Menambahkan produk ke favorit
    @Insert
    suspend fun insertFavorite(favorite: FavoriteEntity)

    // DELETE: Menghapus produk dari favorit
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    // DELETE: Menghapus semua produk favorit
    @Query("DELETE FROM favorites")
    suspend fun clearFavorites()
}