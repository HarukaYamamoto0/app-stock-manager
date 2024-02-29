package com.harukadev.stockmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.harukadev.stockmanager.R
import com.harukadev.stockmanager.data.ProductData
import com.harukadev.stockmanager.viewholder.ProductViewHolder

class ProductAdapter(
    private val context: Context,
    private var productList: List<ProductData>,
    private val productClickListener: OnProductClickListener?,
    private val productLongClickListener: OnProductLongClickListener?
) : RecyclerView.Adapter<ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productData = productList[position]
        holder.bind(productData)

        holder.itemView.setOnClickListener {
            productClickListener?.onProductClick(productData)
        }

        holder.itemView.setOnLongClickListener {
            productLongClickListener?.onProductLongClick(productData, holder.itemView)
            true
        }
    }

    override fun getItemCount(): Int = productList.size

    fun setData(newProductList: List<ProductData>) {
        productList = newProductList
        notifyDataSetChanged()
    }

    interface OnProductClickListener {
        fun onProductClick(product: ProductData)
    }

    interface OnProductLongClickListener {
        fun onProductLongClick(product: ProductData, productView: View)
    }
}
