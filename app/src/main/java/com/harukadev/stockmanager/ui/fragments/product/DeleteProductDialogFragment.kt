package com.harukadev.stockmanager.ui.fragments.product

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.harukadev.stockmanager.data.SectorData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class DeleteProductDialogFragment : DialogFragment(), CoroutineScope {

    private lateinit var confirmButton: TextView
    private lateinit var cancelButton: TextView
    private lateinit var product: ProductData
    private var deleteProductJob: Job? = null
    private var deleteProductListener: DeleteProductListener? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_delete_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        confirmButton = view.findViewById(R.id.button_confirm_delete_product)
        cancelButton = view.findViewById(R.id.button_cancel_delete_product)

        confirmButton.setOnClickListener {
            deleteProduct()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    fun setProductData(productData: ProductData) {
        product = productData
    }

    private fun deleteProduct() {
        deleteProductJob?.cancel()

        deleteProductJob = launch {
            try {
                val success = ProductAPI().deleteProduct(product._id)
                if (success) {
                    deleteProductListener?.onDeletedProduct()
                    dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Falha ao tentar apagar o produto",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Falha ao tentar apagar o produto",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
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
        deleteProductJob?.cancel()
    }

    fun setDeleteProductListener(listener: DeleteProductListener) {
        deleteProductListener = listener
    }

    companion object {
        const val TAG = "DeleteProductDialogFragment"
    }

    interface DeleteProductListener {
        fun onDeletedProduct()
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
