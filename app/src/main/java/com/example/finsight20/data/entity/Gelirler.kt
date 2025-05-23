package com.example.finsight20.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "gelirler",
    foreignKeys = [
        ForeignKey(
            entity = Kullanicilar::class,
            parentColumns = ["kullanici_id"],
            childColumns = ["kullanici_id"]
        )
    ]
)
data class Gelirler(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "gelir_id")
    val gelirId: Int? = null,

    @ColumnInfo(name = "kullanici_id")
    val kullaniciId: Int,

    @ColumnInfo(name = "miktar")
    val miktar: Double,

    @ColumnInfo(name = "kategori")
    val kategori: String,

    @ColumnInfo(name = "aciklama")
    val aciklama: String? = null,

    @ColumnInfo(name = "gelir_tarihi")
    val gelirTarihi: String,

    @ColumnInfo(name = "olusturulma_tarihi")
    val olusturulmaTarihi: String?
)