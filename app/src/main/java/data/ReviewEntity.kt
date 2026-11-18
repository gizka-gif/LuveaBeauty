package com.example.luveabeauty.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val userName: String,
    val rating: Float,
    val comment: String,
    val reviewDate: Long = System.currentTimeMillis()
)