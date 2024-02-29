package com.harukadev.stockmanager.ui.fragments.sector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.harukadev.stockmanager.R
import com.harukadev.stockmanager.api.SectorAPI
import com.harukadev.stockmanager.data.SectorData
import kotlinx.coroutines.*
import android.text.InputFilter

class EditProductDialogFragment : DialogFragment() {

    private lateinit var productNameEditText: TextInputEditText
    private lateinit var barcodeEditText: TextInputEditText
    private lateinit var amountEditText: TextInputEditText
    private lateinit var confirmButton: TextView
    private lateinit var cancelButton: TextView

    private lateinit var produc: ProductData

    private var editSectorJob: Job? = null
    private var editItemListener: EditItemListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_sector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productNameEditText = view.findViewById(R.id.edittext_product_name)
        barcodeEditText = view.findViewById(R.id.edittext_barcode)
        amountEditText = view.findViewById(R.id.edittext_amount)
        confirmButton = view.findViewById(R.id.button_confirm_edit_sector)
        cancelButton = view.findViewById(R.id.button_cancel_edit_sector)
		
		productNameEditText.filters += InputFilter.AllCaps()

        confirmButton.setOnClickListener {
            val name = productNameEditText.text.toString()
            val barcode = barcodeEditText.text.toString().trim()
            val amount = amountEditText.text.toString().toIntOrNull()
			
			if 

            if (name.length > 50) {
                showMessage("Esse nome é muito grande!")
            } else if (name.length < 3) {
                showMessage("Esse nome é muito pequeno!")
            } else if (!isBarcodeValid(barcode)) {
                showMessage("Esse código de barras não é valido!")
            } else if (amount == null) {
                showMessage("A quantidade não é válida!")
            } else {
                createNewProduct(name, barcode, amount)
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    fun setProductData(sectorData: SectorData) {
        sector = sectorData
    }

    private fun editSector(editName: String?, editBarcode: String?, editAmount: Int?) {
        editProductJob?.cancel()

        editProductJob = GlobalScope.launch(Dispatchers.Main) {
            try {
                val success = ProductAPI().updateProduct(
                    ProductData(
                        _id = product._id,
                        name = editName ?: product.name,
                        barcode = editBarcode ?: product.barcode,
                        quantity = editAmount ?: product.quantity,
						sector = product.sector
                    )
                )
                if (success) {
                    editItemListener?.onEditedItem()
                    dismiss()
                } else {
                    showMessage("Falha ao tentar editar o produto")
                }
            } catch (e: Exception) {
                if (isActive) {
                    showMessage("Falha ao tentar editar o produto")
                    e.printStackTrace()
                }
            }
        }
    }
	
	fun setEditItemListener(listener: EditItemListener) {
		editItemListener = listener
    }

    override fun onDestroy() {
        super.onDestroy()
        editProductJob?.cancel()
    }

    interface EditItemListener {
        fun onEditedItem()
    }

    companion object {
        const val TAG = "EditProductDialogFragment"
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
