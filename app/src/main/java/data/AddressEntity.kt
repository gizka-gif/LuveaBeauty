package com.example.luveabeauty.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey
    val id: Int = 1,
    val recipientName: String,
    val phoneNumber: String,
    val fullAddress: String,
    val isMainAddress: Boolean = true
)