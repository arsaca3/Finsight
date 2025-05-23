package com.example.finsight20.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.finsight20.databinding.FragmentGiderekleBinding
import java.util.Calendar
import androidx.lifecycle.ViewModelProvider
import com.example.finsight20.R
import com.example.finsight20.data.entity.Giderler
import com.example.finsight20.ui.viewmodel.GiderEkleViewModel
import com.example.finsight20.util.PrefsManager


class giderekleFragment : Fragment() {

    private lateinit var prefs: PrefsManager
    private lateinit var viewModel: GiderEkleViewModel
    private lateinit var binding: FragmentGiderekleBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGiderekleBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[GiderEkleViewModel::class.java]
        prefs = PrefsManager(requireContext())

        val kategoriler = listOf("Kira", "Market", "Ulaşım", "Sağlık", "Fatura", "Eğlence", "Alışveriş", "Yeme–İçme", "Ev", "Teknoloji", "Diğer")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kategoriler)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerKategori.adapter = adapter

        binding.editDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                R.style.CustomDatePickerDialogTheme,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val dateString = "%02d/%02d/%04d".format(selectedDay, selectedMonth + 1, selectedYear)
                    binding.editDate.setText(dateString)
                },
                year,
                month,
                day
            )
            datePicker.show()
        }
        binding.btnGelirEkle.setOnClickListener {
            val kategori = binding.spinnerKategori.selectedItem.toString()
            val tarih = binding.editDate.text.toString()
            val miktarText = binding.miktar.text.toString()
            val aciklama = binding.aciklama.text.toString()

            if (kategori.isNotEmpty() && tarih.isNotEmpty() && miktarText.isNotEmpty()) {
                val miktar = miktarText.toDoubleOrNull()
                if (miktar != null) {
                    val gider = Giderler(
                        giderId = null,
                        kullaniciId = prefs.getKullaniciId(),
                        miktar = miktar,
                        kategori = kategori,
                        aciklama = aciklama,
                        giderTarihi = "2025-" + tarih.substring(3, 5) + "-" + tarih.substring(0, 2),
                        olusturulmaTarihi = null
                    )

                    viewModel.giderEkle(requireContext(), gider)

                    // Alanları temizle
                    binding.editDate.text?.clear()
                    binding.miktar.text?.clear()
                    binding.aciklama.text?.clear()

                    // Toast göster
                    Toast.makeText(requireContext(), "Gider eklendi", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Geçerli bir miktar girin", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }


}