package com.harukadev.stockmanager.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harukadev.stockmanager.data.SectorData
import com.harukadev.stockmanager.data.ProductData
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.json.JSONArray

class SectorAPI {

    private val apiUrl = "https://app-stock-manager-production.up.railway.app/api/sectors"
    private val TAG = "SectorAPI"
    private val client = HttpClient(Android)
    private val gson = Gson()

    suspend fun getAllSectors(): List<SectorData> {
        return try {
            val response = client.get<String>(apiUrl)
            parseSectors(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all sectors: ${e.message}")
            emptyList()
        }
    }

    suspend fun getSectorById(id: String): SectorData? {
        return try {
            val response = client.get<String>("$apiUrl/$id")
            parseSector(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sector by id: ${e.message}")
            null
        }
    }

    suspend fun createSector(sector: SectorData): Boolean {
        return try {
            val sectorJson = gson.toJson(sector)
            Log.e(TAG, sectorJson)
            client.post<Unit>(apiUrl) {
                contentType(ContentType.Application.Json)
                body = sectorJson
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating sector: ${e.message}")
            false
        }
    }

    suspend fun updateSector(sector: SectorData): Boolean {
        return try {
            val sectorJson = gson.toJson(sector)
            client.put<Unit>("$apiUrl/${sector._id}") {
                contentType(ContentType.Application.Json)
                body = sectorJson
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating sector: ${e.message}")
            false
        }
    }

    suspend fun deleteSector(id: String): Boolean {
        return try {
            client.delete<Unit>("$apiUrl/$id")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting sector: ${e.message}")
            false
        }
    }

    private fun parseSectors(response: String): List<SectorData> {
        val sectorListType = object : TypeToken<List<SectorData>>() {}.type
        return gson.fromJson(response, sectorListType)
    }

    private fun parseSector(response: String): SectorData? {
        return gson.fromJson(response, SectorData::class.java)
    }
}
