package com.example.kosmvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AdminRepository(application)

    private val _authResult = MutableLiveData<Boolean>()
    val authResult: LiveData<Boolean> = _authResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(username, password)
            _authResult.postValue(result)
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            val result = repository.registerAdmin(username, password)
            _authResult.postValue(result)
        }
    }
} 