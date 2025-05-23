package com.example.finsight20.util

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import com.example.finsight20.data.entity.Gelirler
import com.example.finsight20.data.entity.Giderler

object PdfUtils {

    fun olusturGelirGiderPdf(
        context: Context,
        gelirler: List<Gelirler>,
        giderler: List<Giderler>
    ): File {
        val pdfDocument = PdfDocument()
        val paint = android.graphics.Paint()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        var y = 50

        paint.textSize = 16f
        canvas.drawText("Gelirler", 20f, y.toFloat(), paint)
        y += 30

        gelirler.forEach {
            canvas.drawText("- ${it.kategori}: ₺${it.miktar} (${it.gelirTarihi})", 20f, y.toFloat(), paint)
            y += 20
        }

        y += 30
        canvas.drawText("Giderler", 20f, y.toFloat(), paint)
        y += 30

        giderler.forEach {
            canvas.drawText("- ${it.kategori}: ₺${it.miktar} (${it.giderTarihi})", 20f, y.toFloat(), paint)
            y += 20
        }

        pdfDocument.finishPage(page)

        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(directory, "gelir_gider_raporu.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        return file
    }
}