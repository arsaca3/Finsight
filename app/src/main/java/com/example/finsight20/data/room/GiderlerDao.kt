package com.example.finsight20.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.finsight20.data.entity.Giderler

@Dao
interface GiderlerDao {
    @Query("SELECT * FROM Giderler ")
    fun getTumGiderler(): LiveData<List<Giderler>>
    @Insert
    suspend fun giderEkle(gider: Giderler)
    @Query("SELECT * FROM Giderler  WHERE gider_tarihi BETWEEN :baslangic AND :bitis")
    fun getGiderlerByTarihAraligi(baslangic: String, bitis: String): LiveData<List<Giderler>>
    @Query("SELECT * FROM Giderler  WHERE kullanici_id = :kullaniciId")
    fun getGiderlerByKullaniciId(kullaniciId: Int): LiveData<List<Giderler>>
    @Query("SELECT * FROM Giderler  WHERE substr(gider_tarihi, 1, 10) BETWEEN :baslangic AND :bitis AND kullanici_id = :kullaniciId")
    fun getGiderlerByTarihAraligiAndKullaniciId(baslangic: String, bitis: String, kullaniciId: Int): LiveData<List<Giderler>>
    @Query("SELECT * FROM Giderler WHERE kullanici_id = :kullaniciId")
    suspend fun getGiderlerByKullaniciIdNow(kullaniciId: Int): List<Giderler>
    @Query("SELECT * FROM giderler WHERE kullanici_id = :kullaniciId ORDER BY gider_id DESC LIMIT 1")
    fun getSonGider(kullaniciId: Int): Giderler
    @Query("""
    SELECT SUM(miktar) FROM Giderler 
    WHERE kullanici_id = :kullaniciId 
      AND kategori = :kategori 
      AND gider_tarihi LIKE :yilAy || '%'
""")
    suspend fun getKategoriToplamGiderByYilAy(kullaniciId: Int, kategori: String, yilAy: String): Double?
}