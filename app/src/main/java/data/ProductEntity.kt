package com.example.luveabeauty.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String,
    val price: Int,
    val emoji: String,
    val description: String,

    @ColumnInfo(defaultValue = "4.4")
    val rating: Double = 4.4,

    @ColumnInfo(defaultValue = "0")
    val soldCount: Int = 0,

    @ColumnInfo(defaultValue = "20")
    val stock: Int = 20
)