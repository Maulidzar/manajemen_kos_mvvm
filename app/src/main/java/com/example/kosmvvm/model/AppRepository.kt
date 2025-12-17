package com.example.kosmvvm.model

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(application: Application) {
    private val kamarDao: KamarDao
    private val penghuniDao: PenghuniDao

    init {
        val database = AppDatabase.getInstance(application)
        kamarDao = database.kamarDao()
        penghuniDao = database.penghuniDao()
    }

    fun getAllKamarWithPenghuni(): LiveData<List<KamarWithPenghuni>> {
        return kamarDao.getAllKamarWithPenghuni()
    }

    suspend fun deleteKamarDanPenghuni(kamar: KamarEntity, penghuni: PenghuniEntity?) {
        kamarDao.deleteKamar(kamar)
        if (penghuni != null) {
            penghuniDao.deletePenghuni(penghuni)
        }
    }

    suspend fun getKamarWithPenghuniById(idKamar: Int): KamarWithPenghuni? {
        return kamarDao.getKamarWithPenghuniById(idKamar)
    }

    suspend fun updateKamarDanPenghuni(kamar: KamarEntity, penghuni: PenghuniEntity) {
        withContext(Dispatchers.IO) {
            var penghuniIdYgAkanDisimpan: Int? = null

            if (kamar.statusTerisi) {
                if (penghuni.idPenghuni == 0) {
                    val newId = penghuniDao.insertPenghuni(penghuni)
                    penghuniIdYgAkanDisimpan = newId.toInt()
                } else {
                    penghuniDao.updatePenghuni(penghuni)
                    penghuniIdYgAkanDisimpan = penghuni.idPenghuni
                }
            } else {
                if (kamar.idKamar != 0 && kamar.idPenghuni != null) {
                    val oldPenghuni = penghuniDao.getPenghuniById(kamar.idPenghuni!!)
                    if (oldPenghuni != null) {
                        penghuniDao.deletePenghuni(oldPenghuni)
                    }
                }
                penghuniIdYgAkanDisimpan = null
            }

            val finalKamar = kamar.copy(idPenghuni = penghuniIdYgAkanDisimpan)

            if (finalKamar.idKamar == 0) {
                kamarDao.insertKamar(finalKamar)
            } else {
                kamarDao.updateKamar(finalKamar)
            }
        }
    }

    suspend fun getAllKamarWithPenghuniSync(): List<KamarWithPenghuni> {
        return kamarDao.getAllKamarWithPenghuniSync()
    }

    suspend fun updateKamarStatusBayar(idKamar: Int, newStatus: Boolean) {
        withContext(Dispatchers.IO) {
            kamarDao.updateStatusBayar(idKamar, newStatus)
        }
    }

    suspend fun getKamarByNomor(nomorKamar: String): KamarEntity? {
        return kamarDao.getKamarByNomor(nomorKamar)
    }
}