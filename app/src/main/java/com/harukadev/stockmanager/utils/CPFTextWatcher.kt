package com.harukadev.stockmanager.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

object CPFTextWatcher : TextWatcher {
    var isUpdating = false

    override fun beforeTextChanged(
        s: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(
        s: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) {
        if (isUpdating) {
            isUpdating = false
            return
        }

        val hasMask = s.toString().indexOf('.') > -1 || s.toString().indexOf('-') > -1

        var str = s.toString()
            .replace("[.]".toRegex(), "")
            .replace("[-]".toRegex(), "")

        if (count > before) {
            if (str.length > 3 && str[3] != '.') {
                str = str.substring(0, 3) + '.' +
                        str.substring(3)
            }
            if (str.length > 7 && str[7] != '.') {
                str = str.substring(0, 7) + '.' +
                        str.substring(7)
            }
            if (str.length > 11 && str[11] != '-') {
                str = str.substring(0, 11) + '-' +
                        str.substring(11)
            }
            isUpdating = true
            editTextCPF.setText(str)
            editTextCPF.setSelection(editTextCPF.text.length)
        } else {
            isUpdating = true
            editTextCPF.setText(str)
            editTextCPF.setSelection(
                Math.max(
                    0, Math.min(
                        if (hasMask) start - before else start,
                        str.length
                    )
                )
            )
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    lateinit var editTextCPF: EditText

    fun setEditText(editText: EditText) {
        editTextCPF = editText
    }
}
