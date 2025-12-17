package com.example.kosmvvm

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdminRepository(context: Context) {
    private val adminDao = AppDatabase.getInstance(context).adminDao()

    suspend fun registerAdmin(username: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val existing = adminDao.getAdminByUsername(username)
        if (existing != null) return@withContext false
        adminDao.insertAdmin(Admin(username = username, password = password))
        true
    }

    suspend fun login(username: String, password: String): Boolean = withContext(Dispatchers.IO) {
        adminDao.login(username, password) != null
    }
} 