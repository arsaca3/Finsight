package com.example.finsight20.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object MailUtils {

    fun excelMailGonder(context: Context, eposta: String, excelDosyasi: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", excelDosyasi)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(eposta))
            putExtra(Intent.EXTRA_SUBJECT, "Gelir Gider Excel Raporu")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Raporu Paylaş"))
    }



    fun pdfMailGonder(context: Context, aliciMail: String, pdfDosya: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            pdfDosya
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(aliciMail))
            putExtra(Intent.EXTRA_SUBJECT, "Gelir-Gider Raporunuz")
            putExtra(Intent.EXTRA_TEXT, "Lütfen ekteki PDF dosyasını inceleyin.")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Mail uygulaması seçin"))
    }
}