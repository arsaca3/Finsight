package com.example.finsight20.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "harcama_tahminleri",
    foreignKeys = [
        ForeignKey(
            entity = Kullanicilar::class,
            parentColumns = ["kullanici_id"],
            childColumns = ["kullanici_id"],
        )
    ]
)
data class HarcamaTahmin(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tahmin_id")
    val tahminId: Int? = null,

    @ColumnInfo(name = "kullanici_id")
    val kullaniciId: Int,

    @ColumnInfo(name = "kategori")
    val kategori: String,

    @ColumnInfo(name = "tahmini_miktar")
    val tahminiMiktar: Double,

    @ColumnInfo(name = "tahmin_tarihi")
    val tahminTarihi: String,

    @ColumnInfo(name = "olusturulma_tarihi")
    val olusturulmaTarihi: String? = null
)
