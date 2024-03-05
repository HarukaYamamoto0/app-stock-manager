package com.harukadev.stockmanager.utils

import com.google.android.material.textfield.TextInputEditText
import android.text.InputFilter

fun TextInputEditText.addFilter(filter: InputFilter) {
    val existingFilters = filters
    val newFilters = existingFilters.copyOf(existingFilters.size + 1)
    newFilters[existingFilters.size] = filter
    filters = newFilters
}
