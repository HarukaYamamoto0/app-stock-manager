package com.harukadev.stockmanager.ui.fragments.sector

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
import android.widget.ImageView
class NewSectorDialogFragment : DialogFragment() {

    interface NewItemListener {
        fun onNewItemAdded()
    }

    private lateinit var sla: ImageView
    private lateinit var editTextNameOfSector: EditText
    private lateinit var confirmButton: TextView
    private lateinit var cancelButton: TextView
    private var createSectorJob: Job? = null
    private var newItemListener: NewItemListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_new_sector, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        sla = view.findViewById(R.id.sla)
        editTextNameOfSector = view.findViewById(R.id.textInputEditText_name_of_sector)
        confirmButton = view.findViewById(R.id.button_confirm_new_sector)
        cancelButton = view.findViewById(R.id.button_cancel_new_sector)

        sla.setImageResource(R.drawable.ic_spa)
        confirmButton.setOnClickListener {
            val sectorName = editTextNameOfSector.text.toString().trim()
            if (sectorName.isEmpty()) {
                showMessage("Diga o nome do setor")
            } else if (sectorName.length > 19) {
                showMessage("Esse nome é muito grande!")
            } else if (sectorName.length < 3) {
                showMessage("Esse nome é muito pequeno!")
            } else {
                createNewSector(sectorName)
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    @DelicateCoroutinesApi
    private fun createNewSector(sectorName: String) {
        createSectorJob?.cancel()

        createSectorJob = GlobalScope.launch(Dispatchers.Main) {
            try {
                val success = SectorAPI().createSector(SectorData(name = sectorName.uppercase()))
                if (success) {
                    newItemListener?.onNewItemAdded()
                    dismiss()
                } else {
                    showMessage("Erro ao tentar criar o novo setor")
                }
            } catch (e: Exception) {
                if (isActive) {
                    showMessage("Erro ao tentar criar o novo setor")
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
        createSectorJob?.cancel()
    }

    fun setNewItemListener(listener: NewItemListener) {
        newItemListener = listener
    }

    companion object {
        const val TAG = "NewSectorDialogFragment"
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
