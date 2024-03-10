package com.harukadev.stockmanager.provider

import android.content.Context
import androidx.annotation.DrawableRes

object IconProvider {
	
	fun getDrawableList(context: Context): List<Int> {
		val drawableList: MutableList<Int> = mutableListOf()
		
		drawableList.add(getDrawableId(context, "ac_unit"))
		drawableList.add(getDrawableId(context, "cleaning_services"))
		drawableList.add(getDrawableId(context, "grocery"))
		drawableList.add(getDrawableId(context, "local_bar"))
		drawableList.add(getDrawableId(context, "local_cafe"))
		drawableList.add(getDrawableId(context, "local_dining"))
		drawableList.add(getDrawableId(context, "local_florist"))
		drawableList.add(getDrawableId(context, "local_pizza"))
		drawableList.add(getDrawableId(context, "local_see"))
		drawableList.add(getDrawableId(context, "shopping_basket"))
		drawableList.add(getDrawableId(context, "shopping_cart"))
		drawableList.add(getDrawableId(context, "spa"))
		drawableList.add(getDrawableId(context, "storefront"))
		
		return drawableList
	}
	
	@DrawableRes
    private fun getDrawableId(context: Context, name: String): Int {
        return context.resources.getIdentifier(name, "drawable", context.packageName)
    }
}
