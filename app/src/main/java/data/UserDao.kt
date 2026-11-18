package com.example.luveabeauty.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    // CREATE: Menyimpan akun pengguna
    @Insert
    suspend fun insertUser(user: UserEntity)

    // READ: Mengambil akun berdasarkan email
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // READ: Mengambil akun berdasarkan ID
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserEntity?
}