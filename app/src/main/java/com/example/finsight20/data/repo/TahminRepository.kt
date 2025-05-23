package com.example.finsight20.data.repo


import com.example.finsight20.data.entity.HarcamaTahmin
import com.example.finsight20.data.room.HarcamaTahminDao

class TahminRepository(private val tahminDao: HarcamaTahminDao) {

    suspend fun tahminEkle(tahmin: HarcamaTahmin) {
        tahminDao.tahminEkle(tahmin)
    }
}