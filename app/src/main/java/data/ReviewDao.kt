package com.example.luveabeauty.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    // READ: Menampilkan semua ulasan
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY reviewDate DESC")
    fun getReviewsByProduct(productId: Int): Flow<List<ReviewEntity>>

    // CREATE: Menambahkan ulasan
    @Insert
    suspend fun insertReview(review: ReviewEntity)

    // DELETE: Menghapus ulasan
    @Delete
    suspend fun deleteReview(review: ReviewEntity)

    // READ: Menghitung jumlah ulasan produk
    @Query("SELECT COUNT(*) FROM reviews WHERE productId = :productId")
    suspend fun getReviewCount(productId: Int): Int
}