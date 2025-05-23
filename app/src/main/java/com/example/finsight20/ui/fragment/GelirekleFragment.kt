package com.example.finsight20.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.finsight20.R
import com.example.finsight20.data.entity.Gelirler
import com.example.finsight20.databinding.FragmentGelirekleBinding
import com.example.finsight20.ui.viewmodel.GelirEkleViewModel
import com.example.finsight20.util.PrefsManager
import java.util.Calendar
import java.util.Locale


class gelirekleFragment : Fragment() {


    private lateinit var prefs: PrefsManager
    private lateinit var binding: FragmentGelirekleBinding
    private lateinit var viewModel: GelirEkleViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGelirekleBinding.inflate(inflater, container, false)
        prefs = PrefsManager(requireContext())
        viewModel = ViewModelProvider(this)[GelirEkleViewModel::class.java]

        val kategoriler = listOf("Yemek Kartı", "Maaş", "Freelance", "Burs", "Aile Desteği", "İkinci El", "Diğer")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kategoriler)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerKategori.adapter = adapter

        binding.editDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val locale = Locale("tr", "TR")
            Locale.setDefault(locale)

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
                    val gelir = Gelirler(
                        gelirId = null,
                        kullaniciId = prefs.getKullaniciId(),
                        miktar = miktar,
                        kategori = kategori,
                        aciklama = aciklama,
                        gelirTarihi = "2025-" + tarih.substring(3, 5) + "-" + tarih.substring(0, 2),
                        olusturulmaTarihi = null
                    )
                    viewModel.gelirEkle(gelir)

                    binding.editDate.text?.clear()
                    binding.miktar.text?.clear()
                    binding.aciklama.text?.clear()

                    Toast.makeText(requireContext(), "Gelir eklendi", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Geçerli miktar girin", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }


}