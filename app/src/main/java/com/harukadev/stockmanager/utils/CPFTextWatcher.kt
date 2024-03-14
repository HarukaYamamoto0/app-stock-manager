package com.harukadev.stockmanager.utils

import android.text.Editable
import android.text.TextWatcher

object CPFTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        s?.let {
            val cpf = it.toString().replace("[^0-9]*".toRegex(), "")
            val formattedCpf = StringBuilder()

            for (i in cpf.indices) {
                formattedCpf.append(cpf[i])
                if (i == 2 || i == 5) formattedCpf.append('.')
                if (i == 8) formattedCpf.append('-')
            }

            it.replace(0, it.length, formattedCpf.toString())
        }
    }
}
