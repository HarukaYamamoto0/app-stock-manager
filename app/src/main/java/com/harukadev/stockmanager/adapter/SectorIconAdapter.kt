package com.harukadev.stockmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.harukadev.stockmanager.R
import com.harukadev.stockmanager.data.SectorData
import com.harukadev.stockmanager.viewholder.SectorIconViewHolder

class SectorIconAdapter(
    private val context: Context,
    private var sectorList: List<SectorData>,
    private val sectorClickListener: OnSectorClickListener?,
    private val sectorLongClickListener: OnSectorLongClickListener?
) : RecyclerView.Adapter<SectorIconViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectorIconViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_sector_icon, parent, false)
        return SectorIconViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SectorIconViewHolder, position: Int) {
        val sectorData = sectorList[position]
        holder.bind(sectorData)

        holder.itemView.setOnClickListener {
            sectorClickListener?.onSectorClick(sectorData)
        }

        holder.itemView.setOnLongClickListener {
            sectorLongClickListener?.onSectorLongClick(sectorData)
            true
        }
    }

    override fun getItemCount(): Int = sectorList.size

    fun setData(newSectorList: List<SectorData>) {
        sectorList = newSectorList
        notifyDataSetChanged()
    }

    interface OnSectorClickListener {
        fun onSectorClick(sector: SectorData)
    }

    interface OnSectorLongClickListener {
        fun onSectorLongClick(sector: SectorData)
    }
}
