package com.example.finsight20.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finsight20.R

data class FinansKart(val baslik: String, val miktar: String, val oran: String)

class HomeCardAdapter(private val kartlar: List<FinansKart>) :
    RecyclerView.Adapter<HomeCardAdapter.KartViewHolder>() {

    class KartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val baslik: TextView = view.findViewById(R.id.kartBaslik)
        val miktar: TextView = view.findViewById(R.id.kartMiktar)
        val oran: TextView = view.findViewById(R.id.kartOran)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_finans_kart, parent, false)
        return KartViewHolder(view)
    }

    override fun onBindViewHolder(holder: KartViewHolder, position: Int) {
        val index = position % kartlar.size  // Sonsuz scroll için
        val kart = kartlar[index]
        holder.baslik.text = kart.baslik
        holder.miktar.text = kart.miktar
        holder.oran.text = kart.oran

        when {
            kart.oran.contains("-") -> {
                holder.oran.setTextColor(holder.itemView.context.getColor(R.color.negatif_renk))
            }
            kart.oran.contains("%") || kart.oran.contains("+") -> {
                holder.oran.setTextColor(holder.itemView.context.getColor(R.color.pozitif_renk))
            }
            else -> {
                holder.oran.setTextColor(holder.itemView.context.getColor(R.color.black)) // nötr
            }
        }

    }

    override fun getItemCount(): Int = Int.MAX_VALUE  // Sonsuz gibi davranması için
}