package com.harukadev.stockmanager.utils

import android.text.Editable
import android.text.TextWatcher
import java.text.SimpleDateFormat
import java.util.*

object DateTextWatcher : TextWatcher {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private const val DATE_LENGTH_WITHOUT_SLASH = 8

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        s?.let {
            if (it.length > 0 && (it.length % 3 == 0) && (it.length < DATE_LENGTH_WITHOUT_SLASH)) {
                val insertedChar = it[it.length - 1]
                if ('/' != insertedChar) {
                    it.insert(it.length - 1, "/")
                }
            }
        }
    }

    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    fun parseDate(dateString: String): Date? {
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
}
