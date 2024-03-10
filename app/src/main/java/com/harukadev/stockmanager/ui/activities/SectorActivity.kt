package com.harukadev.stockmanager.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.harukadev.stockmanager.R
import com.harukadev.stockmanager.adapter.ProductAdapter
import com.harukadev.stockmanager.api.ProductAPI
import com.harukadev.stockmanager.api.SectorAPI
import com.harukadev.stockmanager.data.ProductData
import com.harukadev.stockmanager.data.SectorData
import com.harukadev.stockmanager.ui.fragments.product.DeleteProductDialogFragment
import com.harukadev.stockmanager.ui.fragments.product.EditProductDialogFragment
import com.harukadev.stockmanager.ui.fragments.product.NewProductDialogFragment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class SectorActivity : AppCompatActivity(), NewProductDialogFragment.NewProductListener,
    DeleteProductDialogFragment.DeleteProductListener, EditProductDialogFragment.EditItemListener {

    companion object {
        const val DATA_SECTOR = "data_sector"
    }

    private lateinit var searchProductEditText: TextInputEditText
    private lateinit var sectorIconImageView: ImageView
    private lateinit var sectorNameTextView: TextView
    private lateinit var totalProductsTextView: TextView
	private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerViewProductsLayout: RelativeLayout
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var loadingProductsLayout: LinearLayout
    private lateinit var noProductsLayout: LinearLayout
    private lateinit var fab: FloatingActionButton
    private var sectorId: String = ""
    private var allProducts: MutableList<ProductData> = mutableListOf()
    private val productApi = ProductAPI()
    private val sectorApi = SectorAPI()
    private val TAG = "SectorActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sector)

        initializeViews()
        startLoading()
		setupSwipeRefresh()
    }

    private fun initializeViews() {
        searchProductEditText = findViewById(R.id.edittext_search_by_product)
        sectorIconImageView = findViewById(R.id.imageview_sector_icon)
        sectorNameTextView = findViewById(R.id.textview_sector_name)
        totalProductsTextView = findViewById(R.id.textview_total_products)
		swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout_products)
        recyclerViewProductsLayout = findViewById(R.id.layout_recyclerView_products)
        recyclerViewProducts = findViewById(R.id.recycler_view_products)
        loadingProductsLayout = findViewById(R.id.layout_loading_data)
        noProductsLayout = findViewById(R.id.layout_no_products)
        fab = findViewById(R.id.fab_new_product)
    }

    @Suppress("DEPRECATION")
    @OptIn(DelicateCoroutinesApi::class)
    private fun startLoading() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val sectorData = intent.getSerializableExtra(DATA_SECTOR) as? SectorData
                sectorId = sectorData?._id ?: ""
                val sectorResponse = sectorApi.getSectorById(sectorId)
                sectorResponse?.let {
                    displaySectorData(it)
                    setupRecyclerView()
                    loadProducts()
                    setupSearchBar()
                    setFabClickListener()
                } ?: run {
                    showMessage("Não foi possível encontrar dados sobre este setor")
                    finish()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "Unknown error")
                showMessage(getString(R.string.error_generic))
                loadingProductsLayout.visibility = View.GONE
                noProductsLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun displaySectorData(sector: SectorData) {
        sectorNameTextView.text = sector.name
        totalProductsTextView.text = getString(R.string.registered_products, sector.products.size)
    }

    private suspend fun loadProducts() {
        try {
            allProducts = productApi.getAllProductsBySectorId(sectorId).toMutableList()
            adapter.setData(allProducts)

            if (allProducts.isEmpty()) {
                loadingProductsLayout.visibility = View.GONE
                noProductsLayout.visibility = View.VISIBLE
            } else {
                loadingProductsLayout.visibility = View.GONE
                noProductsLayout.visibility = View.GONE
                recyclerViewProductsLayout.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error")
            showMessage(getString(R.string.error_generic))
            loadingProductsLayout.visibility = View.GONE
            noProductsLayout.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerViewProducts.layoutManager = layoutManager
        adapter = ProductAdapter(this, allProducts, object : ProductAdapter.OnProductClickListener {
            override fun onProductClick(product: ProductData) {
                //showMessage(product._id)
            }
        }, object : ProductAdapter.OnProductLongClickListener {
            override fun onProductLongClick(product: ProductData, productView: View) {
                val popup = PopupMenu(this@SectorActivity, productView)
                popup.inflate(R.menu.product_options)
                popup.setOnMenuItemClickListener { menuItem ->
                    val itemId = menuItem.itemId

                    if (itemId == R.id.menu_edit_product) {
                        val dialog = EditProductDialogFragment()
                        dialog.setProductData(product)
                        dialog.setEditItemListener(this@SectorActivity)
                        dialog.show(supportFragmentManager, EditProductDialogFragment.TAG)
                    } else if (itemId == R.id.menu_delete_product) {
                        val dialog = DeleteProductDialogFragment()
                        dialog.setProductData(product)
                        dialog.setDeleteProductListener(this@SectorActivity)
                        dialog.show(supportFragmentManager, DeleteProductDialogFragment.TAG)
                    }

                    true
                }
                popup.show()
            }
        })
        recyclerViewProducts.adapter = adapter
		recyclerViewProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0)
					swipeRefreshLayout.isEnabled = true
				else
					swipeRefreshLayout.isEnabled = false
			}
		})
    }
	
	private fun setupSwipeRefresh() {
		swipeRefreshLayout.setOnRefreshListener {
			startLoading()
			swipeRefreshLayout.isRefreshing = false
		}
	}

    private fun setupSearchBar() {
        searchProductEditText.doOnTextChanged { inputText, _, _, _ ->
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

    private fun setFabClickListener() {
        fab.setOnClickListener {
            val dialog = NewProductDialogFragment()
            dialog.setProductData(sectorId)
            dialog.setNewProductListener(this)
            dialog.show(supportFragmentManager, NewProductDialogFragment.TAG)
        }
    }

    override fun onNewProductAdded() {
        startLoading()
    }

    override fun onDeletedProduct() {
        startLoading()
    }

    override fun onEditedItem() {
        startLoading()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@SectorActivity, message, Toast.LENGTH_SHORT).show()
    }
}
