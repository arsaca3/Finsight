package com.example.finsight20.ui.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finsight20.R
import com.example.finsight20.data.repo.GelirGiderRepository
import com.example.finsight20.data.room.AppDatabase
import com.example.finsight20.databinding.FragmentRaporlarBinding
import com.example.finsight20.ui.viewmodel.GelirGiderGrafikViewModel
import com.example.finsight20.ui.viewmodel.GelirGrafikViewModel
import com.example.finsight20.ui.viewmodel.GiderGrafikViewModel
import com.example.finsight20.util.ExcelUtils
import com.example.finsight20.util.MailUtils
import com.example.finsight20.util.PdfUtils
import com.example.finsight20.util.PrefsManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.button.MaterialButton
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.roundToInt

class RaporlarFragment : Fragment() {

    private lateinit var binding: FragmentRaporlarBinding
    private lateinit var giderViewModel: GiderGrafikViewModel
    private lateinit var gelirViewModel: GelirGrafikViewModel
    private lateinit var viewModel: GelirGiderGrafikViewModel
    private lateinit var etiketler: List<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRaporlarBinding.inflate(inflater, container, false)

        val prefs = PrefsManager(requireContext())
        val kullaniciId = prefs.getKullaniciId()

        val db = AppDatabase.getDatabase(requireContext())
        val gelirDao = db.getGelirlerDao()
        val giderDao = db.getGiderlerDao()
        val kullaniciDao = db.getKullanicilarDao()

        giderViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GiderGrafikViewModel(giderDao, kullaniciId) as T
            }
        })[GiderGrafikViewModel::class.java]

        gelirViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GelirGrafikViewModel(gelirDao, kullaniciId) as T
            }
        })[GelirGrafikViewModel::class.java]

        val repository = GelirGiderRepository(gelirDao, giderDao, kullaniciDao)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GelirGiderGrafikViewModel(repository) as T
            }
        })[GelirGiderGrafikViewModel::class.java]

        setupPieChartGider()
        setupPieChartGelir()
        
        giderViewModel.giderleriYukle()
        gelirViewModel.gelirleriYukle()
        observeGiderData()
        observeGelirData()

        binding.pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is PieEntry) {
                    binding.txtSecilenKategori.text = "${e.label}: ₺%.2f".format(e.value)
                }
            }

            override fun onNothingSelected() {
                binding.txtSecilenKategori.text = "Kategori bilgisi burada görünecek"
            }
        })

        binding.pieChartgelir.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is PieEntry) {
                    binding.txtSecilenKategori3.text = "${e.label}: ₺%.2f".format(e.value)
                }
            }

            override fun onNothingSelected() {
                binding.txtSecilenKategori3.text = "Kategori bilgisi burada görünecek"
            }
        })

        binding.sliderBackground.post {
            val buttonCount = 4
            val totalWidth = binding.btnToggleGroup.width
            val slider = binding.sliderBackground

            slider.layoutParams.width = totalWidth / buttonCount
            slider.requestLayout()
        }

        val tipler = listOf("GUNLUK", "HAFTALIK", "AYLIK", "YILLIK")

        listOf(
            binding.btnGunluk to 0,
            binding.btnHaftalik to 1,
            binding.btnAylik to 2,
            binding.btnYillik to 3
        ).forEach { (buton, index) ->
            buton.setOnClickListener {
                binding.btnToggleGroup.check(buton.id) // butonu seçili yap
                val buttonWidth = binding.btnToggleGroup.width / 4
                val newX = buttonWidth * index

                binding.sliderBackground.animate()
                    .translationX(newX.toFloat())
                    .setDuration(250)
                    .start()

                barChartVeriYukle(tipler[index])
            }
        }

        binding.btnToggleGroup.post {
            val width = binding.btnToggleGroup.width
            binding.sliderBackground.layoutParams.width = width / 4
            binding.sliderBackground.requestLayout()
        }

        binding.button2.setOnClickListener {
            val secenekler = arrayOf("PDF olarak paylaş", "Excel olarak paylaş")

            AlertDialog.Builder(requireContext())
                .setTitle("Rapor Paylaş")
                .setItems(secenekler) { _, which ->
                    when (which) {
                        0 -> paylasPdf()
                        1 -> paylasExcel()
                    }
                }
                .setNegativeButton("İptal", null)
                .show()
        }

        barChartVeriYukle("GUNLUK")

        binding.barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e == null || h == null) return

                val x = h.x.roundToInt().coerceIn(etiketler.indices)
                val dataSetIndex = h.dataSetIndex
                val ay = etiketler.getOrNull(x) ?: "-"
                val miktar = e.y

                val mesaj = when (dataSetIndex) {
                    0 -> "$ay - Gelir: ₺%.2f".format(miktar)
                    1 -> "$ay - Gider: ₺%.2f".format(miktar)
                    else -> ""
                }

                binding.textView12.text = mesaj
            }

            override fun onNothingSelected() {
                binding.textView12.text = ""
            }
        })

        return binding.root
    }

    private fun setupPieChartGider() {
        val pieChart = binding.pieChart
        pieChart.setUsePercentValues(false)
        pieChart.description.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.setCenterText("Gider Dağılımı")
        pieChart.setCenterTextSize(16f)
        pieChart.setCenterTextColor(Color.DKGRAY)
        pieChart.holeRadius = 55f
        pieChart.transparentCircleRadius = 60f
        pieChart.setTouchEnabled(true)

        val legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.textSize = 12f
        legend.form = Legend.LegendForm.CIRCLE
    }

    private fun setupPieChartGelir() {
        val pieChart = binding.pieChartgelir
        pieChart.setUsePercentValues(false)
        pieChart.description.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.setCenterText("Gelir Dağılımı")
        pieChart.setCenterTextSize(16f)
        pieChart.setCenterTextColor(Color.DKGRAY)
        pieChart.holeRadius = 55f
        pieChart.transparentCircleRadius = 60f
        pieChart.setTouchEnabled(true)

        val legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.textSize = 12f
        legend.form = Legend.LegendForm.CIRCLE
    }

    private fun observeGiderData() {
        giderViewModel.kategoriToplamlar.observe(viewLifecycleOwner) { liste ->
            if (!liste.isNullOrEmpty()) {
                val sortedList = liste.sortedByDescending { it.second }
                val top7 = sortedList.take(7)
                val otherTotal = sortedList.drop(7).sumOf { it.second }

                val entries = ArrayList<PieEntry>()
                top7.forEach { entries.add(PieEntry(it.second.toFloat(), it.first)) }
                if (otherTotal > 0) entries.add(PieEntry(otherTotal.toFloat(), "Diğer"))

                val dataSet = PieDataSet(entries, "Giderler").apply {
                    valueTextSize = 0f
                    colors = listOf(
                        Color.parseColor("#6EC6FF"), Color.parseColor("#64B5F6"),
                        Color.parseColor("#5C6BC0"), Color.parseColor("#BA68C8"),
                        Color.parseColor("#FFD54F"), Color.parseColor("#4DB6AC"),
                        Color.parseColor("#FF8A65"), Color.parseColor("#E57373")
                    )
                }

                binding.pieChart.data = PieData(dataSet)
                binding.pieChart.animateY(1200, Easing.EaseInOutQuad)
                binding.pieChart.invalidate()
            }
        }
    }

    private fun observeGelirData() {
        gelirViewModel.kategoriToplamlar.observe(viewLifecycleOwner) { liste ->
            if (!liste.isNullOrEmpty()) {
                val sortedList = liste.sortedByDescending { it.second }
                val top7 = sortedList.take(7)
                val otherTotal = sortedList.drop(7).sumOf { it.second }

                val entries = ArrayList<PieEntry>()
                top7.forEach { entries.add(PieEntry(it.second.toFloat(), it.first)) }
                if (otherTotal > 0) entries.add(PieEntry(otherTotal.toFloat(), "Diğer"))

                val dataSet = PieDataSet(entries, "Gelirler").apply {
                    valueTextSize = 0f
                    colors = listOf(
                        Color.parseColor("#81D4FA"), Color.parseColor("#4FC3F7"),
                        Color.parseColor("#29B6F6"), Color.parseColor("#039BE5"),
                        Color.parseColor("#0288D1"), Color.parseColor("#0277BD"),
                        Color.parseColor("#01579B"), Color.parseColor("#B3E5FC")
                    )
                }

                binding.pieChartgelir.data = PieData(dataSet)
                binding.pieChartgelir.animateY(1200, Easing.EaseInOutQuad)
                binding.pieChartgelir.invalidate()
            }
        }
    }

    private fun barChartVeriYukle(zamanTipi: String) {
        val now = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val (baslangic, bitis) = when (zamanTipi) {
            "GUNLUK" -> Pair(now.minusDays(6), now)
            "HAFTALIK" -> Pair(now.minusWeeks(3).with(DayOfWeek.MONDAY), now)
            "AYLIK" -> Pair(now.minusMonths(5).withDayOfMonth(1), now)
            "YILLIK" -> Pair(now.minusYears(1).withDayOfYear(1), now.withMonth(12).withDayOfMonth(31))
            else -> return
        }.let { (start, end) ->
            Pair(start.format(formatter), end.format(formatter))
        }

        val kullaniciId = PrefsManager(requireContext()).getKullaniciId()

        viewModel.getGelirler(baslangic, bitis, kullaniciId).observe(viewLifecycleOwner) { gelirList ->
            viewModel.getGiderler(baslangic, bitis, kullaniciId).observe(viewLifecycleOwner) { giderList ->

                val mapGelir = gelirList.groupBy { tarihEtiketle(it.gelirTarihi, zamanTipi) }
                    .mapValues { it.value.sumOf { g -> g.miktar }.toFloat() }

                val mapGider = giderList.groupBy { tarihEtiketle(it.giderTarihi ?: "", zamanTipi) }
                    .mapValues { it.value.sumOf { g -> g.miktar }.toFloat() }

                etiketler = getEtiketler(zamanTipi)


                val gelirEntries = ArrayList<BarEntry>()
                val giderEntries = ArrayList<BarEntry>()

                etiketler.forEachIndexed { i, etiket ->
                    val x = i.toFloat()
                    gelirEntries.add(BarEntry(x - 0.2f, mapGelir[etiket] ?: 0f))
                    giderEntries.add(BarEntry(x + 0.2f, mapGider[etiket] ?: 0f))
                }

                val gelirSet = BarDataSet(gelirEntries, "Gelir").apply {
                    color = Color.parseColor("#4DB6AC")
                }
                val giderSet = BarDataSet(giderEntries, "Gider").apply {
                    color = Color.parseColor("#F06292")
                }

                val barData = BarData(gelirSet, giderSet).apply {
                    barWidth = 0.18f
                }

                binding.barChart.apply {
                    data = barData
                    data.setDrawValues(false)
                    xAxis.valueFormatter = IndexAxisValueFormatter(etiketler)
                    xAxis.granularity = 1f
                    xAxis.isGranularityEnabled = true
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawGridLines(false)
                    axisLeft.axisMinimum = 0f
                    axisRight.isEnabled = false
                    description.isEnabled = false
                    xAxis.axisMinimum = -0.5f
                    xAxis.axisMaximum = etiketler.size.toFloat() - 0.5f
                    animateY(1000)
                    invalidate()
                }
            }
        }
    }

    private fun getEtiketler(tip: String): List<String> {
        val now = LocalDate.now()
        return when (tip) {
            "GUNLUK" -> (6 downTo 0).map {
                now.minusDays(it.toLong()).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("tr"))
            }

            "HAFTALIK" -> listOf("1. Hafta", "2. Hafta", "3. Hafta", "4. Hafta")

            "AYLIK" -> (5 downTo 0).map {
                now.minusMonths(it.toLong()).month.getDisplayName(TextStyle.SHORT, Locale("tr"))
            }

            "YILLIK" -> listOf(
                now.minusYears(1).year.toString(),
                now.year.toString()
            )

            else -> emptyList()
        }
    }


    private fun tarihEtiketle(tarih: String, tip: String): String {
        if (tarih.isBlank()) return "Bilinmiyor"

        val date = try {
            LocalDate.parse(tarih)
        } catch (e: Exception) {
            return "Bilinmiyor"
        }

        val now = LocalDate.now()

        return when (tip) {
            "GUNLUK" -> date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("tr"))

            "HAFTALIK" -> {
                val startOfRange = now.minusWeeks(3).with(DayOfWeek.MONDAY)
                val currentWeekStart = date.with(DayOfWeek.MONDAY)
                val haftaIndex = ChronoUnit.WEEKS.between(startOfRange, currentWeekStart).toInt()
                if (haftaIndex in 0..3) "${haftaIndex + 1}. Hafta" else "Bilinmiyor"
            }

            "AYLIK" -> {
                val monthDiff = ChronoUnit.MONTHS.between(
                    now.minusMonths(5).withDayOfMonth(1),
                    date.withDayOfMonth(1)
                ).toInt()
                if (monthDiff in 0..5) date.month.getDisplayName(TextStyle.SHORT, Locale("tr")) else "Bilinmiyor"
            }

            "YILLIK" -> {
                val yearStr = date.year.toString()
                val etiketler = listOf(
                    now.minusYears(1).year.toString(),
                    now.year.toString()
                )
                if (yearStr in etiketler) yearStr else "Bilinmiyor"
            }

            else -> "Bilinmiyor"
        }
    }

    private fun paylasPdf() {
        viewModel.getKullaniciEposta(requireContext()).observe(viewLifecycleOwner) { eposta ->
            val gelirler = viewModel.tumGelirler.value ?: emptyList()
            val giderler = viewModel.tumGiderler.value ?: emptyList()
            val pdfDosyasi = PdfUtils.olusturGelirGiderPdf(requireContext(), gelirler, giderler)
            MailUtils.pdfMailGonder(requireContext(), eposta, pdfDosyasi)
        }
    }

    private fun paylasExcel() {
        viewModel.getKullaniciEposta(requireContext()).observe(viewLifecycleOwner) { eposta ->
            val gelirler = viewModel.tumGelirler.value ?: emptyList()
            val giderler = viewModel.tumGiderler.value ?: emptyList()
            val excelDosyasi = ExcelUtils.olusturGelirGiderExcel(requireContext(), gelirler, giderler)
            MailUtils.excelMailGonder(requireContext(), eposta, excelDosyasi)
        }
    }
}
