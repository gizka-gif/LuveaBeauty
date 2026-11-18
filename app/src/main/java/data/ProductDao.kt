
package com.example.luveabeauty.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Int): ProductEntity?

    @Query("SELECT * FROM products WHERE name = :productName LIMIT 1")
    suspend fun getProductByName(productName: String): ProductEntity?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :keyword || '%' ORDER BY id DESC")
    fun searchProducts(keyword: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY id DESC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Insert
    suspend fun insertProduct(product: ProductEntity)

    @Insert
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}
