package com.harukadev.stockmanager.ui.fragments.product

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.harukadev.stockmanager.R
import com.harukadev.stockmanager.api.ProductAPI
import com.harukadev.stockmanager.data.ProductData
import com.harukadev.stockmanager.ui.activities.BarcodeScannerActivity
import kotlinx.coroutines.*


class EditProductDialogFragment : DialogFragment() {

    private lateinit var productNameEditText: TextInputEditText
    private lateinit var barcodeEditText: TextInputEditText
    private lateinit var readBarcodeButton: TextView
    private lateinit var amountEditText: TextInputEditText
    private lateinit var confirmButton: TextView
    private lateinit var cancelButton: TextView

    private lateinit var product: ProductData

    private var editProductJob: Job? = null
    private var editItemListener: EditItemListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        productNameEditText = view.findViewById(R.id.edittext_product_name)
        barcodeEditText = view.findViewById(R.id.edittext_barcode)
        amountEditText = view.findViewById(R.id.edittext_amount)
        confirmButton = view.findViewById(R.id.button_confirm_edit_product)
        cancelButton = view.findViewById(R.id.button_cancel_edit_product)
        readBarcodeButton = view.findViewById(R.id.read_barcode_text)

        readBarcodeButton.paintFlags = readBarcodeButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        productNameEditText.filters += InputFilter.AllCaps()

        confirmButton.setOnClickListener {
            val name = productNameEditText.text.toString()
            val barcode = barcodeEditText.text.toString().trim()
            val amount = amountEditText.text.toString().toIntOrNull()

            if (name.length > 50) {
                showMessage("Esse nome é muito grande!")
            } else if (name.length < 3) {
                showMessage("Esse nome é muito pequeno!")
            } else if (!isBarcodeValid(barcode)) {
                showMessage("Esse código de barras não é valido!")
            } else if (amount == null) {
                showMessage("A quantidade não é válida!")
            } else {
                editProduct(name, barcode, amount)
            }
        }

        readBarcodeButton.setOnClickListener {
            val intent = Intent(requireContext(), BarcodeScannerActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, REQUEST_CODE_BARCODE_SCANNER)
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    fun setProductData(productData: ProductData) {
        product = productData
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun editProduct(editName: String?, editBarcode: String?, editAmount: Int?) {
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
        const val TAG = "NewProductDialogFragment"
        private const val REQUEST_CODE_BARCODE_SCANNER = 1001
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_BARCODE_SCANNER && resultCode == Activity.RESULT_OK) {
            val barcodeValue = data?.getStringExtra(BarcodeScannerActivity.EXTRA_BARCODE_VALUE)
            barcodeEditText.setText(barcodeValue)
        }
    }

    private fun isBarcodeValid(barcode: String): Boolean {
        val barcodePattern = Regex("^[0-9]{12,13}\$")
        return barcode.matches(barcodePattern)
    }
}
