package com.example.finsight20.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finsight20.data.room.GiderlerDao


class GiderGrafikViewModel(
    private val giderlerDao: GiderlerDao,
    private val kullaniciId: Int
) : ViewModel() {

    private val _kategoriToplamlar = MutableLiveData<List<Pair<String, Double>>>()
    val kategoriToplamlar: LiveData<List<Pair<String, Double>>> = _kategoriToplamlar

    fun giderleriYukle() {
        giderlerDao.getGiderlerByKullaniciId(kullaniciId).observeForever { liste ->
            val gruplanmis = liste
                .groupBy { it.kategori }
                .mapValues { entry -> entry.value.sumOf { it.miktar } }
                .map { Pair(it.key, it.value) }

            _kategoriToplamlar.postValue(gruplanmis)
        }
    }
}
