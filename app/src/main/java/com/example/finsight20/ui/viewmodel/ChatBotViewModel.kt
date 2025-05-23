package com.example.finsight20.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.finsight20.data.entity.Gelirler
import com.example.finsight20.data.room.AppDatabase
import com.example.finsight20.ml.IntentClassifier
import com.example.finsight20.util.PrefsManager
import java.time.LocalDate

class ChatBotViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val db = AppDatabase.getDatabase(context)
    private val gelirDao = db.getGelirlerDao()
    private val giderDao = db.getGiderlerDao()
    private val tahminDao = db.getHarcamaTahminDao()
    private val prefs = PrefsManager(context)
    private val classifier = IntentClassifier(context)
    val kullaniciId = prefs.getKullaniciId()

    private val aktifGirdiState = MutableLiveData<String?>(null)
    fun setAktifGirdi(state: String?) { aktifGirdiState.value = state }
    fun getAktifGirdi(): String? = aktifGirdiState.value

    suspend fun getBotResponse(input: String): String {
        val bugun = LocalDate.now().toString()
        val simdikiYilAy = bugun.substring(0, 7)

        val temizInput = input.trim().replace("\"", "")
        val intent = classifier.predictIntent(temizInput)

        // GELİR EKLEME MODE AKTİFSE
        if (aktifGirdiState.value == "gelirEkle") {
            val regex = Regex("(\\d+(\\.\\d{1,2})?)\\s+(.+)")
            val match = regex.find(temizInput)
            if (match != null) {
                val miktar = match.groupValues[1].toDouble()
                val aciklama = match.groupValues[3]
                val gelir = Gelirler(
                    kullaniciId = kullaniciId,
                    gelirTarihi = bugun,
                    miktar = miktar,
                    aciklama = aciklama,
                    kategori = "Diğer",
                    olusturulmaTarihi = bugun
                )
                gelirDao.gelirEkle(gelir)
                setAktifGirdi(null)
                return "✅ $miktar TL gelir eklendi: $aciklama"
            } else {
                return "❗ Lütfen 'miktar açıklama' şeklinde yazın. Örn: `2500 maaş`"
            }
        }

        // ML İLE TAHMİN EDİLEN INTENT'E GÖRE CEVAP
        return when (intent) {
            "gelirEkle" -> {
                setAktifGirdi("gelirEkle")
                "💼 Gelir eklemek için miktar ve açıklama girin. Örn: `2500 maaş`"
            }

            "gelirSorgu" -> {
                val toplam = gelirDao.getToplamGelirByYilAy(kullaniciId, simdikiYilAy) ?: 0.0
                "💰 Bu ay toplam ${"%.2f".format(toplam)} TL geliriniz oldu."
            }

            "giderSorgu" -> {
                val giderler = giderDao.getGiderlerByKullaniciIdNow(kullaniciId)
                    .filter { it.giderTarihi == bugun }
                val toplam = giderler.sumOf { it.miktar }
                if (toplam > 0)
                    "📅 Bugün toplam ${"%.2f".format(toplam)} TL harcadınız."
                else
                    "🕊 Bugün hiç harcama yapılmamış."
            }

            "bütçe" -> {
                val hedefler = tahminDao.getTahminlerByKullaniciIdNow(kullaniciId)
                if (hedefler.isEmpty()) return "📭 Tanımlı bütçe hedefi yok."
                val raporlar = hedefler.map {
                    val harcanan = giderDao.getKategoriToplamGiderByYilAy(kullaniciId, it.kategori, simdikiYilAy) ?: 0.0
                    val oran = if (it.tahminiMiktar != 0.0) (harcanan / it.tahminiMiktar) * 100 else 0.0
                    "• ${it.kategori}: ${"%.0f".format(harcanan)} TL / ${it.tahminiMiktar} TL (%${"%.0f".format(oran)})"
                }
                "📊 Bütçe Durumu:\n" + raporlar.joinToString("\n")
            }

            "tasarruf" -> {
                val gelir = gelirDao.getToplamGelirByYilAy(kullaniciId, simdikiYilAy) ?: 0.0
                val giderler = giderDao.getGiderlerByKullaniciIdNow(kullaniciId)
                    .filter { it.giderTarihi?.startsWith(simdikiYilAy) == true }
                val gider = giderler.sumOf { it.miktar }
                val fark = gelir - gider
                if (fark >= 0)
                    "💵 Bu ay ${"%.2f".format(fark)} TL tasarruf ettiniz."
                else
                    "📉 Bu ay ${"%.2f".format(-fark)} TL fazla harcadınız."
            }

            "yardim" -> """
🤖 Komutlar:
• maaş aldım → gelir ekleme
• bu ay kaç lira kazandım → gelir sorgu
• bugün ne harcadım → günlük gider sorgu
• tasarruf ettim mi → gelir-gider farkı
• bütçem ne durumda → planlı harcama durumu
""".trimIndent()

            else -> {
                setAktifGirdi(null)
                "🤔 Ne demek istediğinizi anlayamadım. `yardim` yazarak komutları görebilirsiniz."
            }
        }
    }
}
