package com.example.kosmvvm

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomRepository(context: Context) {
    private val roomDao = AppDatabase.getInstance(context).roomDao()

    suspend fun getRoomsByAdminId(adminId: String): List<RoomEntity> = withContext(Dispatchers.IO) {
        roomDao.getRoomsByAdminId(adminId)
    }

    suspend fun getRoomById(id: String, adminId: String): RoomEntity? = withContext(Dispatchers.IO) {
        roomDao.getRoomById(id, adminId)
    }

    suspend fun insertRoom(room: RoomEntity) = withContext(Dispatchers.IO) {
        roomDao.insertRoom(room)
    }

    suspend fun updateRoom(room: RoomEntity) = withContext(Dispatchers.IO) {
        roomDao.updateRoom(room)
    }

    suspend fun deleteRoom(room: RoomEntity) = withContext(Dispatchers.IO) {
        roomDao.deleteRoom(room)
    }

    /*suspend fun insertRooms(rooms: List<RoomEntity>) = withContext(Dispatchers.IO) {
        roomDao.insertRooms(rooms)
    }

    suspend fun deleteRoomByKey(roomKey: Int, adminId: String) = withContext(Dispatchers.IO) {
        roomDao.deleteRoomByKey(roomKey, adminId)
    }

    // Legacy method for backward compatibility
    suspend fun getAllRooms(): List<RoomEntity> = withContext(Dispatchers.IO) {
        roomDao.getAllRooms()
    }*/
} 