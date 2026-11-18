package com.example.luveabeauty.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val productName: String,
    val category: String,
    val price: Int,
    val emoji: String
)