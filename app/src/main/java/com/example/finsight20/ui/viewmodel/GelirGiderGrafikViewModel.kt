package com.example.finsight20.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.finsight20.data.entity.Gelirler
import com.example.finsight20.data.entity.Giderler
import com.example.finsight20.data.repo.GelirGiderRepository
import com.example.finsight20.util.PrefsManager

class GelirGiderGrafikViewModel(
    private val repository: GelirGiderRepository
) : ViewModel() {

    fun getGelirler(baslangic: String, bitis: String, kullaniciId: Int): LiveData<List<Gelirler>> {
        return repository.getGelirlerByTarihAraligiAndKullaniciId(baslangic, bitis, kullaniciId)
    }

    fun getGiderler(baslangic: String, bitis: String, kullaniciId: Int): LiveData<List<Giderler>> {
        return repository.getGiderlerByTarihAraligiAndKullaniciId(baslangic, bitis, kullaniciId)
    }

    fun getKullaniciEposta(context: Context): LiveData<String> {
        val prefs = PrefsManager(context)
        val kullaniciId = prefs.getKullaniciId()
        return repository.getKullaniciEposta(kullaniciId)
    }

    val tumGelirler: LiveData<List<Gelirler>> = repository.getTumGelirler()
    val tumGiderler: LiveData<List<Giderler>> = repository.getTumGiderler()
}