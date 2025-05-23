package com.example.finsight20.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.finsight20.databinding.ActivityGirisyabmaBinding
import com.example.finsight20.ui.viewmodel.LoginViewModel
import com.example.finsight20.util.PrefsManager
import kotlinx.coroutines.launch

class girisyabmaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGirisyabmaBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGirisyabmaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]


        binding.hesabolustur.setOnClickListener {
            val intent = Intent(this@girisyabmaActivity, loginActivity::class.java)
            startActivity(intent)
        }


        binding.button3.setOnClickListener {
            val email = binding.editText.text.toString().trim()
            val sifre = binding.editTextTextPassword5.text.toString().trim()

            if (email.isBlank() || sifre.isBlank()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                android.util.Log.d("GIRIS", "Girilen Email: '$email' | Şifre: '$sifre'")

                val sonuc = loginViewModel.girisYap(email, sifre)

                android.util.Log.d("GIRIS", "Sonuç Listesi: ${loginViewModel.girisYapanKullanici}")
                if (sonuc) {
                    val kullanici = loginViewModel.girisYapanKullanici // bunu ViewModel'dan çekmen gerek
                    if (kullanici != null) {
                        val prefs = PrefsManager(this@girisyabmaActivity)
                        prefs.setKullaniciId(kullanici.kullaniciId)
                    }
                    Toast.makeText(this@girisyabmaActivity, "Giriş başarılı", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@girisyabmaActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@girisyabmaActivity, "E-posta veya şifre hatalı", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}