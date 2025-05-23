package com.example.finsight20.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finsight20.data.entity.Giderler
import com.example.finsight20.data.room.AppDatabase
import com.example.finsight20.util.NotificationUtils
import kotlinx.coroutines.launch


class GiderEkleViewModel(application: Application) : AndroidViewModel(application) {
    private val giderlerDao = AppDatabase.getDatabase(application).getGiderlerDao()

    fun giderEkle(context: Context, gider: Giderler) {
        viewModelScope.launch {
            giderlerDao.giderEkle(gider)

            val db = AppDatabase.getDatabase(context)
            val tahminDao = db.getHarcamaTahminDao()
            val giderDao = db.getGiderlerDao()
            val prefs = com.example.finsight20.util.PrefsManager(context)

            val tahminler = tahminDao.getTahminlerByKullaniciIdNow(gider.kullaniciId)
            val giderler = giderDao.getGiderlerByKullaniciIdNow(gider.kullaniciId)

            val bugun = java.time.LocalDate.now().toString()

            tahminler.forEach { tahmin ->
                if (tahmin.kategori == gider.kategori && gider.giderTarihi!! <= tahmin.tahminTarihi) {

                    val toplam = giderler
                        .filter {
                            it.kategori == tahmin.kategori &&
                                    it.giderTarihi != null &&
                                    it.giderTarihi >= bugun &&
                                    it.giderTarihi <= tahmin.tahminTarihi
                        }
                        .sumOf { it.miktar }

                    Log.d("TahminDebug", "Kategori: ${tahmin.kategori}, Tahmini Miktar: ${tahmin.tahminiMiktar}, Toplam Gider: $toplam")

                    if (tahmin.tahminiMiktar > 0) {
                        val yuzde = (toplam / tahmin.tahminiMiktar) * 100

                        if (prefs.getBildirimTercihi()) {
                            when {
                                yuzde >= 100 -> {
                                    NotificationUtils.gonder(
                                        context,
                                        "Hedef aşıldı!",
                                        "${tahmin.kategori} için harcamanız hedefinizi aştı!"
                                    )
                                }
                                yuzde >= 80 -> {
                                    NotificationUtils.gonder(
                                        context,
                                        "Hedefin %80’i aşıldı",
                                        "${tahmin.kategori} için harcamanız hedefin %80’ini geçti."
                                    )
                                }
                                yuzde >= 50 -> {
                                    NotificationUtils.gonder(
                                        context,
                                        "Hedefin %50’si tamamlandı",
                                        "${tahmin.kategori} için hedefinizin %50’sine ulaştınız."
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}