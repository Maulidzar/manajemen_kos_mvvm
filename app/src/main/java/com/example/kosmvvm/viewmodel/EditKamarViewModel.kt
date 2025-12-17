package com.example.kosmvvm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kosmvvm.model.AppRepository
import com.example.kosmvvm.model.KamarEntity
import com.example.kosmvvm.model.KamarWithPenghuni
import com.example.kosmvvm.model.PenghuniEntity
import kotlinx.coroutines.launch

sealed class SaveResult {
    object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}

class EditKamarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val _kamarWithPenghuni = MutableLiveData<KamarWithPenghuni?>()
    val kamarWithPenghuni: LiveData<KamarWithPenghuni?> = _kamarWithPenghuni
    private val _saveStatus = MutableLiveData<SaveResult>()
    val saveStatus: LiveData<SaveResult> = _saveStatus

    fun loadKamarById(idKamar: Int) {
        viewModelScope.launch {
            _kamarWithPenghuni.postValue(repository.getKamarWithPenghuniById(idKamar))
        }
    }

    fun updateKamar(kamar: KamarEntity, penghuni: PenghuniEntity) {
        viewModelScope.launch {
            val existingKamar = repository.getKamarByNomor(kamar.nomorKamar)
            if (existingKamar != null && existingKamar.idKamar != kamar.idKamar) {
                _saveStatus.postValue(SaveResult.Error("Kamar '${kamar.nomorKamar}' sudah ada!"))
                return@launch
            }
            repository.updateKamarDanPenghuni(kamar, penghuni)
            _saveStatus.postValue(SaveResult.Success)
        }
    }
}