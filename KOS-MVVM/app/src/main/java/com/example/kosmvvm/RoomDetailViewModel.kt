package com.example.kosmvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RoomDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RoomRepository(application)
    private val _room = MutableLiveData<RoomEntity>()
    val room: LiveData<RoomEntity> = _room

    fun loadRoom(roomId: String, adminId: String) {
        viewModelScope.launch {
            val room = repository.getRoomById(roomId, adminId)
            if (room != null) {
                _room.value = room
            }
        }
    }

    /*fun setRoom(room: RoomEntity) {
        _room.value = room
    }

    fun updateOccupancy(isOccupied: Boolean) {
        _room.value = _room.value?.copy(isOccupied = isOccupied)
    }

    fun updatePayment(isPaid: Boolean, lastPaymentDate: String?) {
        _room.value = _room.value?.copy(
            isPaid = isPaid,
            lastPaymentDate = if (isPaid && !lastPaymentDate.isNullOrBlank()) lastPaymentDate else null
        )
    }

    fun updateMaintenance(maintenanceNeeds: String?) {
        _room.value = _room.value?.copy(maintenanceNeeds = maintenanceNeeds)
    }

    fun updateAllFields(isOccupied: Boolean, isPaid: Boolean, lastPaymentDate: String?, maintenanceNeeds: String?) {
        val currentRoom = _room.value
        if (currentRoom != null) {
            _room.value = currentRoom.copy(
                isOccupied = isOccupied,
                isPaid = if (isOccupied) isPaid else null,
                lastPaymentDate = if (isOccupied && isPaid && !lastPaymentDate.isNullOrBlank()) lastPaymentDate else null,
                maintenanceNeeds = maintenanceNeeds
            )
        }
    }*/
} 