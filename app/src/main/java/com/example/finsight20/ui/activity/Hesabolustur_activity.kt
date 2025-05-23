package com.example.finsight20.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.finsight20.data.entity.Kullanicilar
import com.example.finsight20.data.room.AppDatabase
import com.example.finsight20.databinding.ActivityHesabolusturBinding
import kotlinx.coroutines.launch

class hesabolustur_activity : AppCompatActivity() {

    private lateinit var binding: ActivityHesabolusturBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHesabolusturBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bugun = java.time.LocalDate.now().toString()
        val gelenMail = intent.getStringExtra("mail") ?: ""
        binding.mail.text = gelenMail

        // GİRİŞE DÖN linki
        binding.giris.setOnClickListener {
            startActivity(Intent(this, girisyabmaActivity::class.java))
            finish()
        }

        // KAYIT OLUŞTUR
        binding.hesabGiris.setOnClickListener {
            val sifre = binding.editTextTextPassword.text.toString().trim()
            val sifreTekrar = binding.editTextTextPassword2.text.toString().trim()

            if (sifre.isBlank() || sifreTekrar.isBlank()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (sifre != sifreTekrar) {
                Toast.makeText(this, "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(this@hesabolustur_activity)
                val yeniKullanici = Kullanicilar(
                    kullaniciAd = gelenMail.substringBefore("@"),
                    ePosta = gelenMail,
                    sifre = sifre,
                    olusturulmaTarihi = bugun,
                    guncellemeTarihi = bugun
                )
                db.getKullanicilarDao().kullaniciEkle(yeniKullanici)
                Toast.makeText(this@hesabolustur_activity, "Hesap oluşturuldu", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@hesabolustur_activity, girisyabmaActivity::class.java))
                finish()
            }
        }
    }
}
