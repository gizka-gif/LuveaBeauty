package com.example.luveabeauty.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val productName: String,
    val category: String,
    val price: Int,
    val emoji: String,
    val quantity: Int = 1
)
