package com.example.finsight20.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.finsight20.data.entity.Gelirler

@Dao
interface GelirlerDao {
    @Query("SELECT * FROM gelirler")
    fun getTumGelirler(): LiveData<List<Gelirler>>
    @Insert
    suspend fun gelirEkle(gelir: Gelirler)
    @Query("SELECT * FROM gelirler WHERE gelir_tarihi BETWEEN :baslangic AND :bitis")
    fun getGelirlerByTarihAraligi(baslangic: String, bitis: String): LiveData<List<Gelirler>>
    @Query("SELECT * FROM gelirler WHERE kullanici_id = :kullaniciId")
    fun getGelirlerByKullaniciId(kullaniciId: Int): LiveData<List<Gelirler>>
    @Query("SELECT * FROM gelirler WHERE substr(gelir_tarihi, 1, 10) BETWEEN :baslangic AND :bitis AND kullanici_id = :kullaniciId")
    fun getGelirlerByTarihAraligiAndKullaniciId(baslangic: String, bitis: String, kullaniciId: Int): LiveData<List<Gelirler>>
    @Query("""
    SELECT SUM(miktar) FROM gelirler 
    WHERE kullanici_id = :kullaniciId 
      AND gelir_tarihi LIKE :yilAy || '%'
""")
    suspend fun getToplamGelirByYilAy(kullaniciId: Int, yilAy: String): Double?


}