package com.harukadev.stockmanager.ui.activities

import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harukadev.stockmanager.R
import com.harukadev.stockmanager.adapter.ProductAdapter
import com.harukadev.stockmanager.data.ProductData
import com.bumptech.glide.Glide
import android.view.ViewStub
import android.widget.Toast
import android.util.Log
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.textfield.TextInputEditText
import com.harukadev.stockmanager.api.ProductAPI
import com.harukadev.stockmanager.data.SectorData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SectorActivity : AppCompatActivity() {

    companion object {
        const val DATA_SECTOR = "data_product"
    }

    private lateinit var searchProducteditText: TextInputEditText
    private lateinit var sectorIconImageView: ImageView
    private lateinit var totalProductsTextView: TextView
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var viewStubContainer: ViewStub
    private lateinit var sectorId: String
    private var allProducts: MutableList<ProductData> = mutableListOf()
    private val productApi = ProductAPI()
    private val TAG = "SectorActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sector)

        initializeViews()
        displaySectorData()
        setupRecyclerView()
        setupSearchBar()
        loadProducts()
    }

    private fun initializeViews() {
        searchProducteditText = findViewById(R.id.edittext_search_by_product)
        sectorIconImageView = findViewById(R.id.imageview_sector_icon)
        totalProductsTextView = findViewById(R.id.textview_total_products)
        recyclerViewProducts = findViewById(R.id.recycler_view_products)
        viewStubContainer = findViewById(R.id.viewStub_container)
    }

    private fun displaySectorData() {
        val product = intent.getSerializableExtra(DATA_SECTOR) as? SectorData
        product?.let {
            Glide.with(this@SectorActivity)
                .load(it.icon)
                .into(sectorIconImageView)

            totalProductsTextView.text = getString(R.string.registered_products, it.products.size)
            sectorId = it.id
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadProducts() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                allProducts = productApi.getAllProductsBySectorId(sectorId).toMutableList()
                adapter.setData(allProducts)

                if (allProducts.size == 0) {
                    viewStubContainer.layoutResource = R.layout.layout_no_data
                    viewStubContainer.inflate()
                }

                viewStubContainer.visibility = View.GONE
                recyclerViewProducts.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "Unknown error")
                showMessage(getString(R.string.error_generic))

                viewStubContainer.layoutResource = R.layout.layout_no_data
                viewStubContainer.inflate()
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerViewProducts.layoutManager = layoutManager
        recyclerViewProducts.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.HORIZONTAL
            )
        )
        adapter = ProductAdapter(
            this,
            allProducts,
            object : ProductAdapter.OnProductClickListener {
                override fun onProductClick(product: ProductData) {
                    // Implementar o que acontece ao clicar em um produto
                }
            },
            object : ProductAdapter.OnProductLongClickListener {
                override fun onProductLongClick(product: ProductData) {
                    showMessage(product.name)
                }
            }
        )
        recyclerViewProducts.adapter = adapter
    }

    private fun setupSearchBar() {
        searchProducteditText.doOnTextChanged { inputText, _, _, _ ->
            val filteredList = if (inputText.isNullOrEmpty()) {
                allProducts
            } else {
                allProducts.filter { product ->
                    product.name.contains(inputText.toString(), ignoreCase = true)
                }
            }
            adapter.setData(filteredList)
        }
    }


    private fun showMessage(message: String) {
        Toast.makeText(this@SectorActivity, message, Toast.LENGTH_SHORT).show()
    }
}
