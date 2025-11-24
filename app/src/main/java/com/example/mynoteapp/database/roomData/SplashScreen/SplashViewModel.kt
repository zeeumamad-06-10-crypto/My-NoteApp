package com.example.mynoteapp.database.roomData.SplashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    var isLoading = true
        private set

    init {
        viewModelScope.launch {
            delay(2000) // Fake loading time
            isLoading = false
        }
    }
}
