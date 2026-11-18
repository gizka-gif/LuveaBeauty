package com.example.luveabeauty.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("""
        SELECT * FROM orders
        WHERE status = :status
        ORDER BY orderDate DESC
    """)
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Query("""
    SELECT * FROM orders
    WHERE orderNumber = :orderNumber
""")
    suspend fun getOrdersByNumber(
        orderNumber: String
    ): List<OrderEntity>

    @Insert
    suspend fun insertOrder(order: OrderEntity)

    @Insert
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Query("""
        UPDATE orders
        SET status = :newStatus
        WHERE orderNumber = :orderNumber
    """)
    suspend fun updateOrderStatus(
        orderNumber: String,
        newStatus: String
    )

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Int)

    @Query("DELETE FROM orders WHERE orderNumber = :orderNumber")
    suspend fun deleteOrderByNumber(orderNumber: String)

    @Query("DELETE FROM orders")
    suspend fun clearOrders()
}