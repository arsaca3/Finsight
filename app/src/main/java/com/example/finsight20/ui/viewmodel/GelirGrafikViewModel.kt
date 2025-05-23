package com.example.finsight20.ui.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finsight20.data.room.GelirlerDao


class GelirGrafikViewModel(
    private val gelirlerDao: GelirlerDao,
    private val kullaniciId: Int
) : ViewModel() {

    val kategoriToplamlar = MutableLiveData<List<Pair<String, Double>>>()

    fun gelirleriYukle() {
        gelirlerDao.getGelirlerByKullaniciId(kullaniciId).observeForever { gelirList ->
            val toplamlar = gelirList
                .groupBy { it.kategori }
                .map { (kategori, list) -> Pair(kategori, list.sumOf { it.miktar }) }

            kategoriToplamlar.value = toplamlar
        }
    }
}