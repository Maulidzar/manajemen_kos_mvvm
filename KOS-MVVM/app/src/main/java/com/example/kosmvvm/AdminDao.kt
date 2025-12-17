package com.example.kosmvvm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AdminDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAdmin(admin: Admin)

    @Query("SELECT * FROM admins WHERE username = :username LIMIT 1")
    suspend fun getAdminByUsername(username: String): Admin?

    @Query("SELECT * FROM admins WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): Admin?
} 