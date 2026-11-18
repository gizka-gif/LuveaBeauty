package com.example.luveabeauty.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {

    // READ: Mengambil alamat utama
    @Query("SELECT * FROM addresses WHERE id = 1 LIMIT 1")
    fun getMainAddress(): Flow<AddressEntity?>

    // CREATE / REPLACE: Menyimpan alamat utama
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAddress(address: AddressEntity)

    // UPDATE: Mengubah alamat utama
    @Update
    suspend fun updateAddress(address: AddressEntity)

    // DELETE: Menghapus alamat utama
    @Query("DELETE FROM addresses WHERE id = 1")
    suspend fun deleteAddress()
}