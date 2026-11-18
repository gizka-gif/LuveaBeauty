package com.example.luveabeauty.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderNumber: String,
    val productId: Int,
    val productName: String,
    val price: Int,
    val quantity: Int,
    val totalPrice: Int,
    val address: String,
    val paymentMethod: String,
    val status: String = "Belum Bayar",
    val orderDate: Long = System.currentTimeMillis()
)