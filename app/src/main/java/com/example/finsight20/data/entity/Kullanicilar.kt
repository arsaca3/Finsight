package com.example.finsight20.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kullanicilar")
data class Kullanicilar(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "kullanici_id")
    val kullaniciId: Int = 0,

    @ColumnInfo(name = "kullanici_ad")
    val kullaniciAd: String,

    @ColumnInfo(name = "e_posta")
    val ePosta: String,

    @ColumnInfo(name = "sifre")
    val sifre: String,

    @ColumnInfo(name = "olusturulma_tarihi")
    val olusturulmaTarihi: String?,

    @ColumnInfo(name = "guncelleme_tarihi")
    val guncellemeTarihi: String?
)