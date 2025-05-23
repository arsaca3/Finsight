package com.example.finsight20.ui.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finsight20.data.entity.HarcamaTahmin
import com.example.finsight20.data.repo.TahminRepository
import kotlinx.coroutines.launch



class TahminViewModel(private val repository: TahminRepository) : ViewModel() {


    fun tahminEkle(tahmin: HarcamaTahmin) = viewModelScope.launch {
        repository.tahminEkle(tahmin)
    }


}