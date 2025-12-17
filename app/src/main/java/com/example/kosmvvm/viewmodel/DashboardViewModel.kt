package com.example.kosmvvm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kosmvvm.model.AppRepository
import com.example.kosmvvm.model.KamarEntity
import com.example.kosmvvm.model.KamarWithPenghuni
import com.example.kosmvvm.model.PenghuniEntity
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val _searchQuery = MutableLiveData<String?>()
    private val _filter = MutableLiveData("Semua")
    private val _butuhMaintenance = MutableLiveData<Boolean>(false)
    private val allKamar: LiveData<List<KamarWithPenghuni>> = repository.getAllKamarWithPenghuni()

    val kamarList: LiveData<List<KamarWithPenghuni>> = MediatorLiveData<List<KamarWithPenghuni>>().apply {
        addSource(allKamar) { kamar ->
            value = filterAndSearchKamar(kamar, _searchQuery.value, _filter.value, _butuhMaintenance.value)
        }

        addSource(_searchQuery) { query ->
            value = filterAndSearchKamar(allKamar.value, query, _filter.value, _butuhMaintenance.value)
        }

        addSource(_filter) { filter ->
            value = filterAndSearchKamar(allKamar.value, _searchQuery.value, filter, _butuhMaintenance.value)
        }

        addSource(_butuhMaintenance) { maintenance ->
            value = filterAndSearchKamar(allKamar.value, _searchQuery.value, _filter.value, maintenance)
        }
    }

    private fun filterAndSearchKamar(
        kamar: List<KamarWithPenghuni>?,
        query: String?,
        filter: String?,
        butuhMaintenance: Boolean?
    ): List<KamarWithPenghuni> {
        var filteredList = kamar ?: emptyList()

        when (filter) {
            "Kosong" -> filteredList = filteredList.filter { !it.kamar.statusTerisi }
            "Belum Bayar" -> filteredList = filteredList.filter { it.kamar.statusTerisi && it.kamar.statusBayar == false }
        }

        if (butuhMaintenance == true) {
            filteredList = filteredList.filter { !it.kamar.statusMaintenance.isNullOrBlank() }
        }

        if (!query.isNullOrBlank()) {
            filteredList = filteredList.filter { data ->
                val kamarNomorMatch = data.kamar.nomorKamar.contains(query, ignoreCase = true)
                val kamarDisplayMatch = "Kamar ${data.kamar.nomorKamar}".contains(query, ignoreCase = true)
                val penghuniMatch = data.penghuni?.namaPenghuni?.contains(query, ignoreCase = true) == true
                kamarNomorMatch || kamarDisplayMatch || penghuniMatch
            }
        }
        return filteredList
    }

    fun setSearchQuery(query: String?) {
        _searchQuery.value = query
    }

    fun setCombinedFilter(filter: String, butuhMaintenance: Boolean) {
        _filter.value = filter
        _butuhMaintenance.value = butuhMaintenance
    }

    fun deleteKamar(kamar: KamarEntity, penghuni: PenghuniEntity?) {
        viewModelScope.launch {
            repository.deleteKamarDanPenghuni(kamar, penghuni)
        }
    }
}