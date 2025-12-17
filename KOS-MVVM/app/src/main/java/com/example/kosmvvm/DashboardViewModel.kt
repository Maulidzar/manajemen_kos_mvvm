package com.example.kosmvvm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RoomRepository(application)
    private val _allRooms = MutableLiveData<List<RoomEntity>>()
    private val _searchQuery = MutableLiveData<String?>()
    private val _filter = MutableLiveData<String>("All")
    private val _needsMaintenance = MutableLiveData<Boolean>(false)

    val rooms: LiveData<List<RoomEntity>> = MediatorLiveData<List<RoomEntity>>().apply {
        addSource(_allRooms) { rooms ->
            value = filterAndSearchRooms(rooms, _searchQuery.value, _filter.value, _needsMaintenance.value)
        }
        addSource(_searchQuery) { query ->
            value = filterAndSearchRooms(_allRooms.value, query, _filter.value, _needsMaintenance.value)
        }
        addSource(_filter) { filter ->
            value = filterAndSearchRooms(_allRooms.value, _searchQuery.value, filter, _needsMaintenance.value)
        }
        addSource(_needsMaintenance) { maintenance ->
            value = filterAndSearchRooms(_allRooms.value, _searchQuery.value, _filter.value, maintenance)
        }
    }
    
    private var currentAdminId: String? = null

    fun setCurrentAdmin(adminId: String) {
        currentAdminId = adminId
        loadRoomsForAdmin()
    }

    private fun loadRoomsForAdmin() {
        viewModelScope.launch {
            val adminId = currentAdminId ?: return@launch
            val rooms = repository.getRoomsByAdminId(adminId)
            Log.d("DashboardViewModel", "Rooms for $adminId: $rooms")
            if (rooms.isEmpty()) {
                // Insert only one default room for new accounts
                val defaultRoom = RoomEntity(
                    id = "1",
                    name = "Room 1", 
                    isOccupied = false, // Always available at first
                    isPaid = null, 
                    lastPaymentDate = null, 
                    maintenanceNeeds = null,
                    adminId = adminId // Associate with current admin
                )
                repository.insertRoom(defaultRoom)
                _allRooms.postValue(listOf(defaultRoom))
            } else {
                _allRooms.postValue(rooms)
            }
        }
    }

    private fun filterAndSearchRooms(
        rooms: List<RoomEntity>?,
        query: String?,
        filter: String?,
        needsMaintenance: Boolean?
    ): List<RoomEntity> {
        var filteredList = rooms ?: emptyList()

        // Apply filter
        when (filter) {
            "Not Occupied" -> {
                filteredList = filteredList.filter { !it.isOccupied }
            }
            "Occupied, Not Paid" -> {
                filteredList = filteredList.filter { it.isOccupied && it.isPaid == false }
            }
        }

        // Apply needs maintenance filter
        if (needsMaintenance == true) {
            filteredList = filteredList.filter { !it.maintenanceNeeds.isNullOrBlank() }
        }

        // Apply search query
        if (!query.isNullOrBlank()) {
            filteredList = filteredList.filter { it.name.contains(query, ignoreCase = true) }
        }
        
        return filteredList
    }


    fun setSearchQuery(query: String?) {
        _searchQuery.value = query
    }

    fun setCombinedFilter(filter: String, needsMaintenance: Boolean) {
        _filter.value = filter
        _needsMaintenance.value = needsMaintenance
    }

    fun updateRoom(updatedRoom: RoomEntity) {
        viewModelScope.launch {
            // Ensure the room belongs to current admin
            val adminId = currentAdminId ?: return@launch
            Log.d("DashboardViewModel", "Updating room: $updatedRoom")
            val roomWithAdminId = updatedRoom.copy(adminId = adminId)
            Log.d("DashboardViewModel", "Room with adminId: $roomWithAdminId")
            repository.updateRoom(roomWithAdminId)
            // Reload rooms to get the updated list
            val updatedRooms = repository.getRoomsByAdminId(adminId)
            Log.d("DashboardViewModel", "After update, rooms: $updatedRooms")
            _allRooms.postValue(updatedRooms)
        }
    }

    fun addNewRoom() {
        viewModelScope.launch {
            val adminId = currentAdminId ?: return@launch
            val currentRooms = _allRooms.value ?: emptyList()
            val newRoomNumber = currentRooms.size + 1
            val newRoom = RoomEntity(
                id = newRoomNumber.toString(),
                name = "Room $newRoomNumber",
                isOccupied = false,
                isPaid = null,
                lastPaymentDate = null,
                maintenanceNeeds = null,
                adminId = adminId // Associate with current admin
            )
            repository.insertRoom(newRoom)
            val updatedRooms = repository.getRoomsByAdminId(adminId)
            _allRooms.postValue(updatedRooms)
        }
    }

    fun deleteRoom(room: RoomEntity) {
        viewModelScope.launch {
            val adminId = currentAdminId ?: return@launch
            repository.deleteRoom(room)
            val updatedRooms = repository.getRoomsByAdminId(adminId)
            _allRooms.postValue(updatedRooms)
        }
    }

    fun logout() {
        currentAdminId = null
        _allRooms.value = emptyList()
    }
}