package com.harukadev.stockmanager.ui.fragments.sector

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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

class EditSectorDialogFragment : DialogFragment() {

    private lateinit var editTextNameOfSector: EditText
    private lateinit var confirmButton: TextView
    private lateinit var cancelButton: TextView
    private lateinit var sector: SectorData
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

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        editTextNameOfSector = view.findViewById(R.id.textInputEditText_name_of_sector)
        confirmButton = view.findViewById(R.id.button_confirm_edit_sector)
        cancelButton = view.findViewById(R.id.button_cancel_edit_sector)

        editTextNameOfSector.filters += InputFilter.AllCaps()
        editTextNameOfSector.setText(sector.name)


        confirmButton.setOnClickListener {
            val sectorName = editTextNameOfSector.text.toString().trim()
            if (sectorName.isEmpty()) {
                showMessage("Por favor, digite o nome do setor")
            } else if (sectorName.length > 19) {
                showMessage("O nome do setor é muito longo!")
            } else if (sectorName.length < 3) {
                showMessage("Esse nome é muito pequeno!")
            } else {
                editSector(sectorName)
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    fun setSectorData(sectorData: SectorData) {
        sector = sectorData
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun editSector(sectorName: String?) {
        editSectorJob?.cancel()

        editSectorJob = GlobalScope.launch(Dispatchers.Main) {
            try {
                val success = SectorAPI().updateSector(
                    SectorData(
                        _id = sector._id,
                        name = sectorName ?: sector.name,
                        icon = sector.icon,
                        products = sector.products
                    )
                )
                if (success) {
                    editItemListener?.onEditedItem()
                    dismiss()
                } else {
                    showMessage("Falha ao tentar editar o setor")
                }
            } catch (e: Exception) {
                if (isActive) {
                    showMessage("Falha ao tentar editar o setor")
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
        editSectorJob?.cancel()
    }

    interface EditItemListener {
        fun onEditedItem()
    }

    companion object {
        const val TAG = "EditSectorDialogFragment"
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
