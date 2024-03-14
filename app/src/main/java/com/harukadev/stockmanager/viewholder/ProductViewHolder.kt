package com.harukadev.stockmanager.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.harukadev.stockmanager.R
import com.harukadev.stockmanager.data.ProductData

class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val nameView: TextView = itemView.findViewById(R.id.textView_name_of_product)
	private val barcodeView: TextView = itemView.findViewById(R.id.textView_barcode)
	private val amountView: TextView = itemView.findViewById(R.id.textView_amount_of_product)
    private lateinit var productData: ProductData

    fun bind(productData: ProductData) {
        this.productData = productData

        nameView.text = productData.name.uppercase()
		barcodeView.text = productData.barcode
		amountView.text = productData.quantity.toString()
    }
}
