package com.harukadev.stockmanager.data

import java.io.Serializable
import java.util.*
import com.google.gson.annotations.SerializedName

data class SectorData(
    @SerializedName("_id")
    val id: String = UUID.randomUUID().toString(),

    @SerializedName("name")
    val name: String,

    @SerializedName("iconURL")
    val icon: String = "https://imgur.com/1LHRHrv.png",

    @SerializedName("products")
    val products: List<ProductData>
) : Serializable
