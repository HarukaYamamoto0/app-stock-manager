package com.harukadev.stockmanager.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class CPFMask {

    companion object {

        private fun cleanCPF(cpf: String): String {
            return cpf.replace(".", "").replace("-", "")
                    .replace("(", "").replace(")", "")
                    .replace("/", "").replace(" ", "")
                    .replace("*", "")
        }

        fun applyMask(mask: String, editText: EditText): TextWatcher {

            return object : TextWatcher {
                var isUpdating: Boolean = false
                var oldString: String = ""

                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val str = cleanCPF(s.toString())
                    var cpfWithMask = ""

                    if (count == 0) // Deletion
                        isUpdating = true

                    if (isUpdating) {
                        oldString = str
                        isUpdating = false
                        return
                    }

                    var i = 0
                    for (m: Char in mask.toCharArray()) {
                        if (m != '#' && str.length > oldString.length) {
                            cpfWithMask += m
                            continue
                        }
                        try {
                            cpfWithMask += str[i]
                        } catch (e: Exception) {
                            break
                        }
                        i++
                    }

                    isUpdating = true
                    editText.setText(cpfWithMask)
                    editText.setSelection(cpfWithMask.length)
                }

                override fun afterTextChanged(editable: Editable) {}
            }
        }

        fun formatCPF(cpf: String): String {
            val cleanedCPF = cleanCPF(cpf)
            return "${cleanedCPF.substring(0, 3)}.${cleanedCPF.substring(3, 6)}.${cleanedCPF.substring(6, 9)}-${cleanedCPF.substring(9)}"
        }
    }
}
