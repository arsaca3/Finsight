package com.example.finsight20.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.finsight20.data.entity.Gelirler
import com.example.finsight20.data.entity.Giderler
import com.example.finsight20.data.room.AppDatabase
import com.example.finsight20.util.PrefsManager

class VarlikViewModel(application: Application) : AndroidViewModel(application) {
    private val gelirlerDao = AppDatabase.getDatabase(application).getGelirlerDao()
    private val giderlerDao = AppDatabase.getDatabase(application).getGiderlerDao()
    private val prefs = PrefsManager(application)
    private val kullaniciId = prefs.getKullaniciId()

    val toplamVarlikVerisi = MediatorLiveData<List<Pair<String, Double>>>()

    fun giderleriAyIleFiltrele(yilAy: String): LiveData<Double> {
        val result = MediatorLiveData<Double>()
        val giderlerLiveData = giderlerDao.getGiderlerByKullaniciId(kullaniciId)

        result.addSource(giderlerLiveData) { giderListesi ->
            val toplam = giderListesi
                .filter { it.giderTarihi?.startsWith(yilAy) == true }
                .sumOf { it.miktar }

            result.value = toplam
        }

        return result
    }

    fun gelirleriAyIleFiltrele(yilAy: String): LiveData<Double> {
        val result = MediatorLiveData<Double>()
        val gelirlerLiveData = gelirlerDao.getGelirlerByKullaniciId(kullaniciId)

        result.addSource(gelirlerLiveData) { gelirList ->
            val toplam = gelirList
                .filter { it.gelirTarihi.startsWith(yilAy) }
                .sumOf { it.miktar }

            result.value = toplam
        }

        return result
    }

    init {
        val gelirlerLiveData = gelirlerDao.getGelirlerByKullaniciId(kullaniciId)
        val giderlerLiveData = giderlerDao.getGiderlerByKullaniciId(kullaniciId)

        toplamVarlikVerisi.addSource(gelirlerLiveData) { gelirler ->
            val giderler = giderlerLiveData.value
            if (giderler != null) hesapla(gelirler, giderler)
        }

        toplamVarlikVerisi.addSource(giderlerLiveData) { giderler ->
            val gelirler = gelirlerLiveData.value
            if (gelirler != null) hesapla(gelirler, giderler)
        }
    }

    private fun hesapla(
        gelirler: List<Gelirler>,
        giderler: List<Giderler>
    ) {
        val gelirMap = gelirler
            .filter { it.gelirTarihi != null }
            .groupBy { it.gelirTarihi!! }
            .mapValues { it.value.sumOf { g -> g.miktar } }

        val giderMap = giderler
            .filter { it.giderTarihi != null }
            .groupBy { it.giderTarihi!! }
            .mapValues { it.value.sumOf { g -> g.miktar } }

        val tumTarihler = (gelirMap.keys + giderMap.keys).distinct().sorted()

        val toplamListe = tumTarihler.map { tarih ->
            val gelir = gelirMap[tarih] ?: 0.0
            val gider = giderMap[tarih] ?: 0.0
            Pair(tarih, gelir - gider)
        }

        toplamVarlikVerisi.value = toplamListe
    }

    fun birikenVarliklarAylik(yilAy: String): LiveData<List<Pair<String, Double>>> {
        val result = MediatorLiveData<List<Pair<String, Double>>>()

        val gelirlerLiveData = gelirlerDao.getGelirlerByKullaniciId(kullaniciId)
        val giderlerLiveData = giderlerDao.getGiderlerByKullaniciId(kullaniciId)

        result.addSource(gelirlerLiveData) { gelirler ->
            val giderler = giderlerLiveData.value
            if (giderler != null) {
                result.value = hesaplaAylik(gelirler, giderler, yilAy)
            }
        }

        result.addSource(giderlerLiveData) { giderler ->
            val gelirler = gelirlerLiveData.value
            if (gelirler != null) {
                result.value = hesaplaAylik(gelirler, giderler, yilAy)
            }
        }

        return result
    }

    private fun hesaplaAylik(
        gelirler: List<Gelirler>,
        giderler: List<Giderler>,
        yilAy: String
    ): List<Pair<String, Double>> {
        val filtreliGelir = gelirler.filter { it.gelirTarihi.startsWith(yilAy) }
        val filtreliGider = giderler.filter { it.giderTarihi?.startsWith(yilAy) == true }

        val gelirMap = filtreliGelir.groupBy { it.gelirTarihi }
            .mapValues { it.value.sumOf { g -> g.miktar } }

        val giderMap = filtreliGider.groupBy { it.giderTarihi!! }
            .mapValues { it.value.sumOf { g -> g.miktar } }

        val tarihler = (gelirMap.keys + giderMap.keys).distinct().sorted()

        var toplam = 0.0
        return tarihler.map { tarih ->
            val gelir = gelirMap[tarih] ?: 0.0
            val gider = giderMap[tarih] ?: 0.0
            toplam += (gelir - gider)
            Pair(tarih, toplam)
        }
    }

    fun giderDegisimiAylarArasi(yilAy: String): LiveData<String> {
        val result = MediatorLiveData<String>()
        val simdikiAy = giderleriAyIleFiltrele(yilAy)
        val oncekiAy = giderleriAyIleFiltrele(birOncekiAy(yilAy))

        result.addSource(simdikiAy) { simdiki ->
            val onceki = oncekiAy.value
            if (onceki != null) {
                result.value = hesaplaDegisim(onceki, simdiki)
            }
        }

        result.addSource(oncekiAy) { onceki ->
            val simdiki = simdikiAy.value
            if (simdiki != null) {
                result.value = hesaplaDegisim(onceki, simdiki)
            }
        }

        return result
    }

    fun gelirDegisimiAylarArasi(yilAy: String): LiveData<String> {
        val result = MediatorLiveData<String>()
        val simdikiAy = gelirleriAyIleFiltrele(yilAy)
        val oncekiAy = gelirleriAyIleFiltrele(birOncekiAy(yilAy))

        result.addSource(simdikiAy) { simdiki ->
            val onceki = oncekiAy.value
            if (onceki != null) {
                result.value = hesaplaDegisim(onceki, simdiki)
            }
        }

        result.addSource(oncekiAy) { onceki ->
            val simdiki = simdikiAy.value
            if (simdiki != null) {
                result.value = hesaplaDegisim(onceki, simdiki)
            }
        }

        return result
    }

    private fun hesaplaDegisim(onceki: Double, simdiki: Double): String {
        return if (onceki == 0.0) {
            "Önceki ay verisi yok"
        } else {
            val fark = ((simdiki - onceki) / onceki * 100).toInt()
            val isaret = if (fark >= 0) "+" else ""
            "$isaret$fark% bir önceki aya göre"
        }
    }

    private fun birOncekiAy(yilAy: String): String {
        val year = yilAy.substring(0, 4).toInt()
        val month = yilAy.substring(5, 7).toInt()

        val prevYear = if (month == 1) year - 1 else year
        val prevMonth = if (month == 1) 12 else month - 1

        return String.format("%04d-%02d", prevYear, prevMonth)
    }

    fun toplamVarlik(): LiveData<Double> {
        val result = MediatorLiveData<Double>()
        val gelirlerLiveData = gelirlerDao.getGelirlerByKullaniciId(kullaniciId)
        val giderlerLiveData = giderlerDao.getGiderlerByKullaniciId(kullaniciId)

        result.addSource(gelirlerLiveData) { gelirler ->
            val giderler = giderlerLiveData.value
            if (giderler != null) {
                val toplamGelir = gelirler.sumOf { it.miktar }
                val toplamGider = giderler.sumOf { it.miktar }
                result.value = toplamGelir - toplamGider
            }
        }

        result.addSource(giderlerLiveData) { giderler ->
            val gelirler = gelirlerLiveData.value
            if (gelirler != null) {
                val toplamGelir = gelirler.sumOf { it.miktar }
                val toplamGider = giderler.sumOf { it.miktar }
                result.value = toplamGelir - toplamGider
            }
        }


        return result
    }
    fun varlikDegisimiAylarArasi(yilAy: String): LiveData<String> {
        val result = MediatorLiveData<String>()
        val simdikiAy = birikenVarliklarAylik(yilAy)
        val oncekiAy = birikenVarliklarAylik(birOncekiAy(yilAy))

        result.addSource(simdikiAy) { simdikiListe ->
            val oncekiListe = oncekiAy.value
            val simdiki = simdikiListe.lastOrNull()?.second
            val onceki = oncekiListe?.lastOrNull()?.second

            if (simdiki == null || onceki == null) {
                result.value = "Önceki ay verisi yok"
            } else {
                result.value = hesaplaDegisim(onceki, simdiki)
            }
        }

        result.addSource(oncekiAy) { oncekiListe ->
            val simdikiListe = simdikiAy.value
            val simdiki = simdikiListe?.lastOrNull()?.second
            val onceki = oncekiListe.lastOrNull()?.second

            if (simdiki == null || onceki == null) {
                result.value = "Önceki ay verisi yok"
            } else {
                result.value = hesaplaDegisim(onceki, simdiki)
            }
        }

        return result
    }
}