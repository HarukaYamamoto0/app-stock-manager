package com.harukadev.stockmanager.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harukadev.stockmanager.R
import com.harukadev.stockmanager.data.SectorData
import android.util.Log

class SectorIconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val iconView: ImageView = itemView.findViewById(R.id.imageview_icon)
    private val nameView: TextView = itemView.findViewById(R.id.textview_name)
    private lateinit var sectorData: SectorData

    fun bind(sectorData: SectorData) {
        this.sectorData = sectorData
        
        Glide.with(itemView.context)
            .load(sectorData.icon)
            .placeholder(R.drawable.ic_sector_default)
            .error(R.drawable.ic_sector_default)
            .into(iconView)

        nameView.text = sectorData.name
    }
}
