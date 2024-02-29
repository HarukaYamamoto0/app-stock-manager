package com.harukadev.stockmanager.ui.fragments.product

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.harukadev.stockmanager.R
import com.harukadev.stockmanager.api.ProductAPI
import com.harukadev.stockmanager.data.ProductData
import com.harukadev.stockmanager.ui.activities.BarcodeScannerActivity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@Suppress("DEPRECATION")
class NewProductDialogFragment : DialogFragment(), CoroutineScope {

    private lateinit var productNameEditText: TextInputEditText
    private lateinit var barcodeEditText: TextInputEditText
    private lateinit var amountEditText: TextInputEditText
    private lateinit var confirmButton: TextView
    private lateinit var cancelButton: TextView
    private lateinit var sectorId: String

    private var createProductJob: Job? = null
    private var newProductListener: NewProductListener? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_new_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        productNameEditText = view.findViewById(R.id.edittext_product_name)
        barcodeEditText = view.findViewById(R.id.edittext_barcode)
        amountEditText = view.findViewById(R.id.edittext_amount)
        confirmButton = view.findViewById(R.id.button_confirm_new_product)
        cancelButton = view.findViewById(R.id.button_cancel_new_product)

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
                createNewProduct(name, barcode, amount)
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        barcodeEditText.setOnClickListener {
            val intent = Intent(requireContext(), BarcodeScannerActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_BARCODE_SCANNER)
        }
    }

    fun setProductData(sectorIdInput: String) {
        sectorId = sectorIdInput
    }

    private fun createNewProduct(name: String, barcode: String, amount: Int) {
        createProductJob?.cancel()

        createProductJob = launch {
            try {
                val success =
                    ProductAPI().createProduct(
                        ProductData(
                            name = name,
                            barcode = barcode,
                            quantity = amount,
                            sector = sectorId
                        )
                    )
                if (success) {
                    newProductListener?.onNewProductAdded()
                    dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Falha ao tentar criar o produto",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Falha ao tentar criar o produto",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_BARCODE_SCANNER && resultCode == Activity.RESULT_OK) {
            val barcodeValue = data?.getStringExtra(BarcodeScannerActivity.EXTRA_BARCODE_VALUE)
            barcodeEditText.setText(barcodeValue)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.animations
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        createProductJob?.cancel()
    }

    fun setNewProductListener(listener: NewProductListener) {
        newProductListener = listener
    }

    companion object {
        const val TAG = "NewProductDialogFragment"
        private const val REQUEST_CODE_BARCODE_SCANNER = 1001
    }

    interface NewProductListener {
        fun onNewProductAdded()
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun isBarcodeValid(barcode: String): Boolean {
        val barcodePattern = Regex("^[0-9]{12,13}\$")
        return barcode.matches(barcodePattern)
    }
}
