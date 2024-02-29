package com.harukadev.stockmanager.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

object EditTextUppercaseHelper {

    fun setUpperCaseTextWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nothing Here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Nothing Here
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text != text.toUpperCase()) {
                    val upperCaseText = text.toUpperCase()
                    editText.apply {
                        setText(upperCaseText)
                        setSelection(length())
                    }
                }
            }
        })
    }
}
