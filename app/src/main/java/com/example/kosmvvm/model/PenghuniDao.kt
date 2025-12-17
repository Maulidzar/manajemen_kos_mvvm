package com.example.kosmvvm.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PenghuniDao {
    @Query("SELECT * FROM penghuni ORDER BY idPenghuni ASC")
    fun getAllPenghuni(): LiveData<List<PenghuniEntity>>

    @Query("SELECT * FROM penghuni WHERE idPenghuni = :idPenghuni LIMIT 1")
    suspend fun getPenghuniById(idPenghuni: Int): PenghuniEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPenghuni(penghuni: PenghuniEntity): Long

    @Update
    suspend fun updatePenghuni(penghuni: PenghuniEntity)

    @Delete
    suspend fun deletePenghuni(penghuni: PenghuniEntity)
}