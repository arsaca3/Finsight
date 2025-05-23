package com.example.finsight20.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.finsight20.data.entity.Kullanicilar

@Dao
interface KullanicilarDao {

    @Query("SELECT * FROM kullanicilar WHERE e_posta = :email AND sifre = :password")
    suspend fun kullaniciGirisi(email: String, password: String): List<Kullanicilar>
    @Query("SELECT e_posta FROM kullanicilar WHERE kullanici_id = :kullaniciId LIMIT 1")
    fun getEpostaById(kullaniciId: Int): LiveData<String>
    @Query("SELECT * FROM kullanicilar WHERE kullanici_id = :id LIMIT 1")
    suspend fun getKullaniciById(id: Int): Kullanicilar
    @Update
    suspend fun updateKullanici(kullanici: Kullanicilar)
    @Insert
    suspend fun kullaniciEkle(kullanici: Kullanicilar)
}