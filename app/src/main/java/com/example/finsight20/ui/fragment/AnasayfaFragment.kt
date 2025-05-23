package com.example.finsight20.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finsight20.adapter.FinansKart
import com.example.finsight20.adapter.HomeCardAdapter
import com.example.finsight20.databinding.FragmentAnasayfaBinding
import com.example.finsight20.ui.viewmodel.VarlikViewModel
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import androidx.activity.OnBackPressedCallback

class AnasayfaFragment : Fragment() {

    private lateinit var binding: FragmentAnasayfaBinding
    private lateinit var varlikViewModel: VarlikViewModel
    private var secilenYil = "2025"

    private val scrollHandler = Handler(Looper.getMainLooper())
    private val scrollRunnable = object : Runnable {
        override fun run() {
            binding.kartRecyclerView.smoothScrollBy(5, 0)
            scrollHandler.postDelayed(this, 60)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAnasayfaBinding.inflate(inflater, container, false)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Geri tuşuna basılınca hiçbir şey yapma
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        varlikViewModel = ViewModelProvider(this, factory)[VarlikViewModel::class.java]

        val yillar = listOf("2025", "2024")
        val aylar = listOf("Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık")

        binding.spinnerAy.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, aylar).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinneryL.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, yillar).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        
        binding.kartRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        scrollHandler.postDelayed(scrollRunnable, 1000)

        binding.spinneryL.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                secilenYil = parent.getItemAtPosition(position).toString()
                binding.spinnerAy.setSelection(0)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spinnerAy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val ay = (position + 1).toString().padStart(2, '0')
                val yilAy = "$secilenYil-$ay"

                varlikViewModel.birikenVarliklarAylik(yilAy).observe(viewLifecycleOwner) { liste ->
                    if (liste.isNullOrEmpty()) {
                        binding.lineChart.clear()
                        binding.lineChart.setNoDataText("Bu ay için grafik verisi yok")
                        binding.lineChart.setNoDataTextColor(Color.CYAN)
                        binding.lineChart.invalidate()
                        Toast.makeText(requireContext(), "Bu ay için veri yok", Toast.LENGTH_SHORT).show()
                        return@observe
                    }

                    val entries = ArrayList<Entry>()
                    val gunler = ArrayList<String>()
                    liste.forEachIndexed { index, pair ->
                        entries.add(Entry(index.toFloat(), pair.second.toFloat()))
                        gunler.add(pair.first.takeLast(2))
                    }

                    val dataSet = LineDataSet(entries, "Toplam Varlık").apply {
                        color = ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light)
                        valueTextSize = 8f
                        setDrawValues(false)
                        setDrawFilled(true)
                        setDrawCircles(true)
                        fillAlpha = 100
                        lineWidth = 2f
                    }

                    binding.lineChart.apply {
                        data = LineData(dataSet)
                        xAxis.apply {
                            xAxis.position = XAxisPosition.BOTTOM
                            valueFormatter = IndexAxisValueFormatter(gunler)
                            granularity = 1f
                            setDrawGridLines(false)
                            labelRotationAngle = -45f
                            setLabelCount(6, true)
                        }
                        axisLeft.setLabelCount(6, true)
                        axisRight.isEnabled = false
                        description.text = "Günlere Göre Biriken Toplam Varlık"
                        animateY(1000)
                        invalidate()
                    }
                }

                val gelirLive = varlikViewModel.gelirleriAyIleFiltrele(yilAy)
                val giderLive = varlikViewModel.giderleriAyIleFiltrele(yilAy)
                val varlikLive = varlikViewModel.birikenVarliklarAylik(yilAy)
                val gelirDegisimLive = varlikViewModel.gelirDegisimiAylarArasi(yilAy)
                val giderDegisimLive = varlikViewModel.giderDegisimiAylarArasi(yilAy)
                val varlikDegisimLive = varlikViewModel.varlikDegisimiAylarArasi(yilAy)

                val sonuc = MediatorLiveData<Unit>()
                var gelir = 0.0
                var gider = 0.0
                var varlik = 0.0
                var gelirDegisim = ""
                var giderDegisim = ""
                var varlikDegisim = ""

                sonuc.addSource(gelirLive) { gelir = it ?: 0.0; sonuc.value = Unit }
                sonuc.addSource(giderLive) { gider = it ?: 0.0; sonuc.value = Unit }
                sonuc.addSource(varlikLive) { liste -> varlik = liste.lastOrNull()?.second ?: 0.0; sonuc.value = Unit }
                sonuc.addSource(gelirDegisimLive) { gelirDegisim = it ?: ""; sonuc.value = Unit }
                sonuc.addSource(giderDegisimLive) { giderDegisim = it ?: ""; sonuc.value = Unit }
                sonuc.addSource(varlikDegisimLive) { varlikDegisim = it ?: ""; sonuc.value = Unit }

                sonuc.observe(viewLifecycleOwner) {
                    val formatter = DecimalFormat("#,###.##", DecimalFormatSymbols(Locale("tr", "TR")))
                    val varlikBaslik = if (varlik >= 0) "Aylık Tasarrufunuz" else "Aylık Zararınız"

                    val kartListesi = listOf(
                        FinansKart("Aylık Giderleriniz", formatter.format(gider), giderDegisim),
                        FinansKart("Aylık Gelirleriniz", formatter.format(gelir), gelirDegisim),
                        FinansKart(varlikBaslik, formatter.format(varlik), if (varlikDegisim.isNotBlank()) varlikDegisim else "Önceki ay verisi yok")
                    )

                    binding.kartRecyclerView.adapter = HomeCardAdapter(kartListesi)
                    binding.kartRecyclerView.scrollToPosition(Int.MAX_VALUE / 2 - Int.MAX_VALUE / 2 % kartListesi.size)


                    // Scroll'u yeniden başlat
                    scrollHandler.removeCallbacks(scrollRunnable)
                    scrollHandler.postDelayed(scrollRunnable, 1000)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val formatter = DecimalFormat("#,###.##", DecimalFormatSymbols(Locale("tr", "TR")))
        varlikViewModel.toplamVarlik().observe(viewLifecycleOwner) { toplam ->
            binding.toplamVarlK.text = "Toplam Varlık: ${formatter.format(toplam ?: 0.0)}₺"
        }

        return binding.root
    }
}
