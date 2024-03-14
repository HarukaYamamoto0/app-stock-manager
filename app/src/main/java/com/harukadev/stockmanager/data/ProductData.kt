package com.harukadev.stockmanager.data

import java.io.Serializable
import java.util.*
import com.google.gson.annotations.SerializedName
import com.harukadev.stockmanager.utils.generateObjectId;

data class ProductData (
    @SerializedName("_id")
    val _id: String = generateObjectId(),
    
    @SerializedName("name")
    var name: String,
    
    @SerializedName("barcode")
    val barcode: String,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("sector")
    val sector: String,
) : Serializable {
    init {
        name = name.uppercase()
    }
}
