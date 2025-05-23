package com.example.finsight20.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finsight20.R
import com.example.finsight20.data.entity.HarcamaTahmin
import com.example.finsight20.data.repo.TahminRepository
import com.example.finsight20.data.room.AppDatabase
import com.example.finsight20.databinding.FragmentButcelemeBinding
import com.example.finsight20.ui.viewmodel.TahminViewModel
import com.example.finsight20.util.PrefsManager
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class ButcelemeFragment : Fragment() {


    private lateinit var binding: FragmentButcelemeBinding
    private lateinit var tahminViewModel: TahminViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentButcelemeBinding.inflate(inflater, container, false)


        val tahminDao = AppDatabase.getDatabase(requireContext()).getHarcamaTahminDao()
        val tahminRepository = TahminRepository(tahminDao)

        tahminViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TahminViewModel(tahminRepository) as T
            }
        })[TahminViewModel::class.java]



        val kategoriler = listOf("Kira", "Market", "Ulaşım", "Sağlık", "Fatura", "Eğlence", "Alışveriş", "Yeme–İçme", "Ev", "Teknoloji", "Diğer")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kategoriler)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerKategori3.adapter = adapter

        binding.editDate3.setOnClickListener {
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
                    binding.editDate3.setText(dateString)
                },
                year,
                month,
                day
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }

        binding.btnGelirEkle3.setOnClickListener {
            val kategori = binding.spinnerKategori3.selectedItem.toString()
            val tarih = binding.editDate3.text.toString()
            val miktar = binding.miktar3.text.toString().toDoubleOrNull()
            val kullaniciId = PrefsManager(requireContext()).getKullaniciId()
            val bugun = LocalDate.now().toString()

            if (kategori.isNotBlank() && tarih.isNotBlank() && miktar != null) {
                val tahmin = HarcamaTahmin(
                    kullaniciId = kullaniciId,
                    kategori = kategori,
                    tahminiMiktar = miktar,
                    tahminTarihi = tarih,
                    olusturulmaTarihi = bugun
                )

                tahminViewModel.tahminEkle(tahmin)
                Toast.makeText(requireContext(), "Tahmin eklendi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

}