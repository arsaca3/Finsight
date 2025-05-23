package com.example.finsight20.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.finsight20.data.entity.Kullanicilar
import com.example.finsight20.databinding.FragmentProfilAyarBinding
import com.example.finsight20.ui.viewmodel.ProfilViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class profilAyarFragment : Fragment() {

    private lateinit var binding: FragmentProfilAyarBinding
    private lateinit var viewModel: ProfilViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfilAyarBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[ProfilViewModel::class.java]

        viewModel.kullanici.observe(viewLifecycleOwner) { kullanici ->
            binding.editName.setText(kullanici.kullaniciAd)
            binding.editMail.setText(kullanici.ePosta)
            binding.editPassword.setText(kullanici.sifre)
        }
        viewModel.kullaniciBilgileriniYukle()

        binding.editNameIcon.setOnClickListener {
            binding.editName.isEnabled = true
            binding.editName.isFocusableInTouchMode = true
            binding.editName.requestFocus()
        }

        binding.editMailIcon.setOnClickListener {
            binding.editMail.isEnabled = true
            binding.editMail.isFocusableInTouchMode = true
            binding.editMail.requestFocus()
        }

        binding.editsifreIcon.setOnClickListener {
            binding.editPassword.isEnabled = true
            binding.editPassword.isFocusableInTouchMode = true
            binding.editPassword.requestFocus()
        }

        binding.button.setOnClickListener {
            val guncelKullanici = Kullanicilar(
                kullaniciId = viewModel.kullanici.value?.kullaniciId ?: return@setOnClickListener,
                kullaniciAd = binding.editName.text.toString(),
                ePosta = binding.editMail.text.toString(),
                sifre = binding.editPassword.text.toString(),
                olusturulmaTarihi = viewModel.kullanici.value?.olusturulmaTarihi,
                guncellemeTarihi = getCurrentTimeStamp()
            )

            viewModel.kullaniciGuncelle(guncelKullanici)
            Toast.makeText(requireContext(), "Kullanıcı bilgileri güncellendi", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun getCurrentTimeStamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }


}