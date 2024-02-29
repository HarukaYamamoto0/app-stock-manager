package com.harukadev.stockmanager.ui.fragments.sector

import android.app.AlertDialog
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
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.harukadev.stockmanager.R
import com.harukadev.stockmanager.api.SectorAPI
import com.harukadev.stockmanager.data.SectorData
import kotlinx.coroutines.*
import android.text.InputFilter

class DeleteSectorDialogFragment : DialogFragment() {

    private lateinit var editTextNameOfSector: EditText
    private lateinit var confirmButton: TextView
    private lateinit var cancelButton: TextView
	
	private lateinit var sector: SectorData

    private var deleteSectorJob: Job? = null
    private var deletedItemListener: DeleteItemListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_delete_sector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextNameOfSector = view.findViewById(R.id.textInputEditText_name_of_sector)
        confirmButton = view.findViewById(R.id.button_confirm_delete_sector)
        cancelButton = view.findViewById(R.id.button_cancel_delete_sector)
		
		editTextNameOfSector.filters += InputFilter.AllCaps()

        confirmButton.setOnClickListener {
			if (editTextNameOfSector.text.toString() == sector.name){
				deleteSector()
			} else {
				showMessage("Esse nome não está correto!")
			}
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }
	
	fun setSectorData(sectorData: SectorData) {
		sector = sectorData
    }

    private fun deleteSector() {
        deleteSectorJob?.cancel()

        deleteSectorJob = GlobalScope.launch(Dispatchers.Main) {
            try {
                val success = SectorAPI().deleteSector(sector._id)
                if (success) {
					deletedItemListener?.onDeletedItem()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Falha ao tentar deletar o setor", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
				if (isActive) {
                    Toast.makeText(requireContext(), "Falha ao tentar deletar o setor", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
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
        deleteSectorJob?.cancel()
    }

    fun setDeleteItemListener(listener: DeleteItemListener) {
		deletedItemListener = listener
    }
	
	companion object {
		const val TAG = "DeleteSectorDialogFragment"
	}
	
	interface DeleteItemListener {
		fun onDeletedItem()
	}
	
	private fun showMessage(message: String) {
		Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
	}
}
