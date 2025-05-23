package com.example.finsight20.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finsight20.data.entity.Gelirler
import com.example.finsight20.data.entity.Giderler
import com.example.finsight20.data.entity.HarcamaTahmin
import com.example.finsight20.data.entity.Kullanicilar

@Database(entities = [Giderler::class, Gelirler::class, HarcamaTahmin::class, Kullanicilar::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getGiderlerDao(): GiderlerDao
    abstract fun getKullanicilarDao(): KullanicilarDao
    abstract fun getGelirlerDao(): GelirlerDao
    abstract fun getHarcamaTahminDao(): HarcamaTahminDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finsight_db"
                )
                    .createFromAsset("findb.sqlite")
                    .fallbackToDestructiveMigration() //bununla veritabanı temizleniyor unutma
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}