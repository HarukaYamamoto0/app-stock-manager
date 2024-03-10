package com.harukadev.iconify

import android.content.Context
import androidx.annotation.DrawableRes

object IconProvider {
    @DrawableRes
    fun getDrawableByName(context: Context, name: String): Int {
		return context.resources.getIdentifier(name, "drawable", context.packageName)
    }
}
