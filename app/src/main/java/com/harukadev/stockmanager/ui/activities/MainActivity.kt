package com.harukadev.stockmanager.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harukadev.stockmanager.R
import com.harukadev.stockmanager.adapter.SectorIconAdapter
import com.harukadev.stockmanager.api.SectorAPI
import com.harukadev.stockmanager.data.SectorData
import com.harukadev.stockmanager.utils.CPFMask
import com.harukadev.stockmanager.utils.SharedPreferencesManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.harukadev.stockmanager.ui.fragments.NewSectorDialogFragment
import android.view.ViewStub
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.DelicateCoroutinesApi

class MainActivity : AppCompatActivity(), NewSectorDialogFragment.NewItemListener {

    private lateinit var editTextSearchSector: TextInputEditText
    private lateinit var btnSeeMore: TextView
    private lateinit var sectorIconRecyclerView: RecyclerView
    private lateinit var adapter: SectorIconAdapter
    private var allSectors: MutableList<SectorData> = mutableListOf()
    private lateinit var layout2RelativeLayout: RelativeLayout
    private lateinit var userAvatarImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var cpfTextView: TextView
    private lateinit var fab: FloatingActionButton
    private lateinit var viewStubContainer: ViewStub
    private var productsToShow: Int = 6
    private val sectorApi = SectorAPI()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferencesManager = SharedPreferencesManager.getInstance(this)
        initializeViews()
        loadUserData()
        setupRecyclerView()
        startLoading()
    }

    private fun initializeViews() {
        editTextSearchSector = findViewById(R.id.edittext_search_by_sector)
        sectorIconRecyclerView = findViewById(R.id.recycleview_sector_icon)
        btnSeeMore = findViewById(R.id.button_see_more_sectors)
        layout2RelativeLayout = findViewById(R.id.layout_2)
        viewStubContainer = findViewById(R.id.viewStub_container)
        fab = findViewById(R.id.fab_new_sector)

        userAvatarImageView = findViewById(R.id.imageview_user_avatar)
        usernameTextView = findViewById(R.id.textview_username)
        cpfTextView = findViewById(R.id.textview_cpf_of_user)

        viewStubContainer.layoutResource = R.layout.layout_loading_data
        viewStubContainer.inflate()
        layout2RelativeLayout.visibility = View.INVISIBLE
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startLoading() {
        GlobalScope.launch(Dispatchers.Main) {
            loadSectors()
            setupSearchBar()
            setupSeeMoreButton()
            setFabClickListener()
        }
    }

    private fun loadUserData() {
        val avatarUrl = sharedPreferencesManager.getString("avatarURL", "https://imgur.com/WRQ2Kbj.png")
        Glide.with(this@MainActivity)
            .load(avatarUrl)
            .into(userAvatarImageView)

        usernameTextView.text = sharedPreferencesManager.getString("name", "???")

        val cpf = sharedPreferencesManager.getString("cpf", "???")
        cpfTextView.text = CPFMask.formatCPF(cpf)
    }

    private suspend fun loadSectors() {
        try {
            allSectors = sectorApi.getAllSectors().toMutableList()
            adapter.setData(allSectors.take(productsToShow))

            if (allSectors.isEmpty()) {
                viewStubContainer.layoutResource = R.layout.layout_no_data
                viewStubContainer.inflate()
            }

            viewStubContainer.visibility = View.GONE
            layout2RelativeLayout.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error")
            Toast.makeText(this@MainActivity, getString(R.string.error_generic), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(this, 3)
        sectorIconRecyclerView.layoutManager = layoutManager
        adapter = SectorIconAdapter(this, allSectors,
            object : SectorIconAdapter.OnSectorClickListener {
                override fun onSectorClick(sector: SectorData) {
                    val intent = Intent(this@MainActivity, SectorActivity::class.java).apply {
                        putExtra(SectorActivity.DATA_SECTOR, sector)
                    }
                    startActivity(intent)
                }
            },
            object : SectorIconAdapter.OnSectorLongClickListener {
                override fun onSectorLongClick(sector: SectorData) {
                    showMessage(sector.name)
                }
            }
        )
        sectorIconRecyclerView.adapter = adapter
    }

    private fun setupSearchBar() {
        editTextSearchSector.doOnTextChanged { inputText, _, _, _ ->
            val filteredList = if (inputText.isNullOrEmpty()) {
                allSectors.take(productsToShow)
            } else {
                allSectors.filter { sector ->
                    sector.name.contains(inputText.toString(), ignoreCase = true)
                }
            }
            productsToShow = filteredList.size
            adapter.setData(filteredList.take(productsToShow))
            updateSeeMoreButtonVisibility()
        }
    }

    private fun setFabClickListener() {
        fab.setOnClickListener {
            val dialog = NewSectorDialogFragment()
            dialog.setNewItemListener(this)
            dialog.show(supportFragmentManager, NewSectorDialogFragment.TAG)
        }
    }

    private fun setupSeeMoreButton() {
        btnSeeMore.setOnClickListener {
            productsToShow = if (productsToShow > 6) 6 else allSectors.size
            val sectorList = allSectors.take(productsToShow)
            adapter.setData(sectorList)
            updateSeeMoreButtonVisibility()
        }
    }

    private fun updateSeeMoreButtonVisibility() {
        btnSeeMore.text = if (productsToShow > 6) getString(R.string.see_less) else getString(R.string.see_more)
    }

    override fun onNewItemAdded() {
        startLoading()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}
