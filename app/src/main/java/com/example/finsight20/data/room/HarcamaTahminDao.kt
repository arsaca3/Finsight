package com.example.finsight20.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.finsight20.data.entity.HarcamaTahmin

@Dao
interface HarcamaTahminDao {
    @Insert
    suspend fun tahminEkle(tahmin: HarcamaTahmin)

    @Query("SELECT * FROM harcama_tahminleri WHERE kullanici_id = :kullaniciId")
    fun getTahminlerByKullaniciId(kullaniciId: Int): LiveData<List<HarcamaTahmin>>
    @Query("SELECT * FROM harcama_tahminleri WHERE kullanici_id = :kullaniciId")
    suspend fun getTahminlerByKullaniciIdNow(kullaniciId: Int): List<HarcamaTahmin>
    @Query("SELECT * FROM harcama_tahminleri WHERE kullanici_id = :kullaniciId LIMIT 1")
    suspend fun getAktifTahmin(kullaniciId: Int): HarcamaTahmin?

}