package com.example.finsight20.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.finsight20.data.entity.Kullanicilar
import com.example.finsight20.data.room.AppDatabase
import com.example.finsight20.util.PrefsManager
import kotlinx.coroutines.launch

class ProfilViewModel(application: Application) : AndroidViewModel(application) {
    private val kullaniciDao = AppDatabase.getDatabase(application).getKullanicilarDao()
    private val prefs = PrefsManager(application)

    private val _kullanici = MutableLiveData<Kullanicilar>()
    val kullanici: LiveData<Kullanicilar> get() = _kullanici

    fun kullaniciBilgileriniYukle() {
        viewModelScope.launch {
            try {
                val id = prefs.getKullaniciId()
                _kullanici.postValue(kullaniciDao.getKullaniciById(id))
            } catch (e: Exception) {
                Log.e("ProfilViewModel", "Kullanıcı verisi yüklenemedi", e)
            }
        }
    }

    fun kullaniciGuncelle(kullanici: Kullanicilar) {
        viewModelScope.launch {
            try {
                kullaniciDao.updateKullanici(kullanici)
                Log.d("ProfilViewModel", "Kullanıcı bilgileri güncellendi.")
            } catch (e: Exception) {
                Log.e("ProfilViewModel", "Güncelleme başarısız", e)
            }
        }
    }
}