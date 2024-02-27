package com.harukadev.stockmanager.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harukadev.stockmanager.data.ProductData
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.http.*

class ProductAPI {

    private val apiUrl = "https://app-stock-manager-production.up.railway.app/api/products/"
    private val TAG = "ProductAPI"
    private val client = HttpClient(Android)
    private val gson = Gson()

    suspend fun getAllProductsBySectorId(sectorId: String): List<ProductData> {
      return try {
        val response = client.get<String>("$apiUrl/$sectorId")
        parseProducts(response)
    } catch (e: Exception) {
        Log.e(TAG, "Error getting all products by sector id: ${e.message}")
        emptyList()
    }
}

    suspend fun getProductById(id: String): ProductData? {
        return try {
            val response = client.get<String>("$apiUrl/$id")
            parseProduct(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting product by id: ${e.message}")
            null
        }
    }

    suspend fun createProduct(product: ProductData): Boolean {
        return try {
            val productJson = gson.toJson(product)
            Log.e(TAG, productJson)
            client.post<Unit>(apiUrl) {
                contentType(ContentType.Application.Json)
                body = productJson
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating product: ${e.message}")
            false
        }
    }

    suspend fun updateProduct(product: ProductData): Boolean {
        return try {
            val productJson = gson.toJson(product)
            client.put<Unit>("$apiUrl/${product._id}") {
                contentType(ContentType.Application.Json)
                body = productJson
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating product: ${e.message}")
            false
        }
    }

    suspend fun deleteProduct(id: String): Boolean {
        return try {
            client.delete<Unit>("$apiUrl/$id")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting product: ${e.message}")
            false
        }
    }

    private fun parseProducts(response: String): List<ProductData> {
        val productListType = object : TypeToken<List<ProductData>>() {}.type
        return gson.fromJson(response, productListType)
    }

    private fun parseProduct(response: String): ProductData? {
        return gson.fromJson(response, ProductData::class.java)
    }
}
