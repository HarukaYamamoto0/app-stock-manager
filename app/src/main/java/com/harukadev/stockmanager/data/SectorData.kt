package com.harukadev.stockmanager.data

import java.io.Serializable
import java.util.*
import com.google.gson.annotations.SerializedName
import com.harukadev.stockmanager.data.ProductData

data class SectorData(
    @SerializedName("_id")
    val _id: String = UUID.randomUUID().toString(),

    @SerializedName("name")
    val name: String,

    @SerializedName("iconURL")
    val icon: String = "https://imgur.com/1LHRHrv.png",

    @SerializedName("products")
    val products: List<ProductData> = listOf()
) : Serializable
