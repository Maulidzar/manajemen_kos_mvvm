package com.example.kosmvvm.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface KamarDao {
    @Transaction
    @Query("SELECT * FROM kamar ORDER BY CAST(nomorKamar AS INT) ASC")
    fun getAllKamarWithPenghuni(): LiveData<List<KamarWithPenghuni>>

    @Transaction
    @Query("SELECT * FROM kamar ORDER BY CAST(nomorKamar AS INT) ASC")
    suspend fun getAllKamarWithPenghuniSync(): List<KamarWithPenghuni>

    @Transaction
    @Query("SELECT * FROM kamar WHERE idKamar = :idKamar LIMIT 1")
    suspend fun getKamarWithPenghuniById(idKamar: Int): KamarWithPenghuni?

    @Query("UPDATE kamar SET statusBayar = :newStatus WHERE idKamar = :idKamar")
    suspend fun updateStatusBayar(idKamar: Int, newStatus: Boolean)

    @Query("SELECT * FROM kamar WHERE nomorKamar = :nomorKamar LIMIT 1")
    suspend fun getKamarByNomor(nomorKamar: String): KamarEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKamar(kamar: KamarEntity)

    @Update
    suspend fun updateKamar(kamar: KamarEntity)

    @Delete
    suspend fun deleteKamar(kamar: KamarEntity)
}