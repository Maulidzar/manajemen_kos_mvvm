package com.example.kosmvvm

import androidx.room.*

@Dao
interface RoomDao {
    @Query("SELECT * FROM room WHERE adminId = :adminId")
    suspend fun getRoomsByAdminId(adminId: String): List<RoomEntity>

    @Query("SELECT * FROM room WHERE id = :id AND adminId = :adminId LIMIT 1")
    suspend fun getRoomById(id: String, adminId: String): RoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity)

    @Update
    suspend fun updateRoom(room: RoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<RoomEntity>)

    @Delete
    suspend fun deleteRoom(room: RoomEntity)

    @Query("DELETE FROM room WHERE roomKey = :roomKey AND adminId = :adminId")
    suspend fun deleteRoomByKey(roomKey: Int, adminId: String)

    /*// Legacy method for backward compatibility
    @Query("SELECT * FROM room")
    suspend fun getAllRooms(): List<RoomEntity>*/
} 