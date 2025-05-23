package com.example.finsight20.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.finsight20.data.entity.Kullanicilar
import com.example.finsight20.data.room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val kullaniciDao = AppDatabase.getDatabase(application).getKullanicilarDao()

    var girisYapanKullanici: Kullanicilar? = null
    suspend fun girisYap(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val sonuc = kullaniciDao.kullaniciGirisi(email, password)
                girisYapanKullanici = sonuc.firstOrNull()
                sonuc.isNotEmpty()
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}