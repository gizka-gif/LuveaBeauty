package com.example.luveabeauty.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // READ: Menampilkan semua isi keranjang
    @Query("SELECT * FROM cart ORDER BY id DESC")
    fun getAllCartItems(): Flow<List<CartEntity>>

    // READ: Mengambil produk tertentu di keranjang
    @Query("SELECT * FROM cart WHERE productId = :productId LIMIT 1")
    suspend fun getCartItem(productId: Int): CartEntity?

    // CREATE: Menambahkan produk ke keranjang
    @Insert
    suspend fun insertCartItem(item: CartEntity)

    // UPDATE: Mengubah jumlah produk
    @Update
    suspend fun updateCartItem(item: CartEntity)

    // DELETE: Menghapus satu produk dari keranjang
    @Delete
    suspend fun deleteCartItem(item: CartEntity)

    // DELETE: Mengosongkan seluruh keranjang
    @Query("DELETE FROM cart")
    suspend fun clearCart()
}