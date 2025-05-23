package com.example.finsight20.util

import android.content.Context
import android.os.Environment
import com.example.finsight20.data.entity.Gelirler
import com.example.finsight20.data.entity.Giderler
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

object ExcelUtils {

    fun olusturGelirGiderExcel(context: Context, gelirler: List<Gelirler>, giderler: List<Giderler>): File {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Gelir Gider Raporu")

        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("Tür")
        header.createCell(1).setCellValue("Kategori")
        header.createCell(2).setCellValue("Tutar")

        var rowIndex = 1

        gelirler.forEach {
            val row = sheet.createRow(rowIndex++)
            row.createCell(0).setCellValue("Gelir")
            row.createCell(1).setCellValue(it.kategori)
            row.createCell(2).setCellValue(it.miktar)
        }

        giderler.forEach {
            val row = sheet.createRow(rowIndex++)
            row.createCell(0).setCellValue("Gider")
            row.createCell(1).setCellValue(it.kategori)
            row.createCell(2).setCellValue(it.miktar)
        }

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "gelir_gider_raporu.xlsx")
        val outputStream = FileOutputStream(file)
        workbook.write(outputStream)
        outputStream.close()
        workbook.close()

        return file
    }
}