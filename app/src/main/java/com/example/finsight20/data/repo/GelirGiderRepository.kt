package com.example.finsight20.data.repo

import androidx.lifecycle.LiveData
import com.example.finsight20.data.entity.Gelirler
import com.example.finsight20.data.entity.Giderler
import com.example.finsight20.data.room.GelirlerDao
import com.example.finsight20.data.room.GiderlerDao
import com.example.finsight20.data.room.KullanicilarDao

class GelirGiderRepository(
    private val gelirlerDao: GelirlerDao,
    private val giderlerDao: GiderlerDao,
    private val kullanicilarDao: KullanicilarDao
) {

    fun getTumGelirler(): LiveData<List<Gelirler>> {
        return gelirlerDao.getTumGelirler()
    }

    fun getTumGiderler(): LiveData<List<Giderler>> {
        return giderlerDao.getTumGiderler()
    }
    fun getKullaniciEposta(kullaniciId: Int): LiveData<String> {
        return kullanicilarDao.getEpostaById(kullaniciId)
    }
    fun getGelirlerByTarihAraligiAndKullaniciId(baslangic: String, bitis: String, kullaniciId: Int): LiveData<List<Gelirler>> {
        return gelirlerDao.getGelirlerByTarihAraligiAndKullaniciId(baslangic, bitis, kullaniciId)
    }

    fun getGiderlerByTarihAraligiAndKullaniciId(baslangic: String, bitis: String, kullaniciId: Int): LiveData<List<Giderler>> {
        return giderlerDao.getGiderlerByTarihAraligiAndKullaniciId(baslangic, bitis, kullaniciId)
    }

}