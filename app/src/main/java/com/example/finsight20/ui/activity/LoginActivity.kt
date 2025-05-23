package com.example.finsight20.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finsight20.databinding.ActivityLoginBinding


class loginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.giris.setOnClickListener {
            val intent = Intent(this@loginActivity, girisyabmaActivity::class.java)
            startActivity(intent)
        }
        val mail = binding.editTextTextEmailAddress

        binding.hesabGiris.setOnClickListener {
            val email = mail.text.toString().trim()
            if (!isValidEmail(email)) {
                Toast.makeText(this, "Lütfen geçerli bir e-posta adresi girin.", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(this@loginActivity, hesabolustur_activity::class.java)
                intent.putExtra("mail", email)
                startActivity(intent)
            }
        }
    }
}