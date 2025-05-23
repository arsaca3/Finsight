package com.example.finsight20.util

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("finsight_prefs", Context.MODE_PRIVATE)

    fun setKullaniciId(id: Int) {
        prefs.edit().putInt("kullanici_id", id).apply()
    }

    fun getKullaniciId(): Int {
        return prefs.getInt("kullanici_id", -1)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
    fun setBildirimTercihi(acik: Boolean) {
        prefs.edit().putBoolean("bildirim_tercihi", acik).apply()
    }

    fun getBildirimTercihi(): Boolean {
        return prefs.getBoolean("bildirim_tercihi", true) // varsayılan: açık
    }
}