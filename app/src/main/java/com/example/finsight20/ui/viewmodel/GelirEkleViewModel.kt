package com.example.finsight20.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finsight20.data.entity.Gelirler
import com.example.finsight20.data.room.AppDatabase
import kotlinx.coroutines.launch

class GelirEkleViewModel(application: Application) : AndroidViewModel(application) {
    private val gelirlerDao = AppDatabase.getDatabase(application).getGelirlerDao()

    fun gelirEkle(gelir: Gelirler) {
        viewModelScope.launch {
            gelirlerDao.gelirEkle(gelir)
        }
    }
}