package com.harukadev.stockmanager.utils

import android.text.InputFilter
import android.text.InputFilter.*

class TextInputFilters {
    companion object {

        fun numbersFilter(): InputFilter {
            return InputFilter { source, start, end, _, _, _ ->
                filterInput(source, start, end) { !Character.isDigit(it) }
            }
        }

        fun allCapsFilter(): InputFilter {
            return AllCaps()
        }

        private fun filterInput(
            source: CharSequence?,
            start: Int,
            end: Int,
            predicate: (Char) -> Boolean
        ): CharSequence? {
            for (i in start until end) {
                if (predicate(source?.get(i) ?: return null)) {
                    return source.subSequence(start, end)
                }
            }
            return null
        }
    }
}
