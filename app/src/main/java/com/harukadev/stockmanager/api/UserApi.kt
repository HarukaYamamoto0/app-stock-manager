package com.harukadev.stockmanager.api

import android.util.Log
import com.harukadev.stockmanager.data.UserData
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.json.JSONArray
import org.json.JSONObject

class UserAPI {

    private val apiUrl = "https://app-stock-manager-production.up.railway.app/api/users/"
    private val TAG = "UserAPI"
    private val client = HttpClient(Android)

    suspend fun getAllUsers(): List<UserData> {
        return try {
            val response = client.get<String>(apiUrl)
            parseUsers(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all users: ${e.message}")
            emptyList()
        }
    }

    suspend fun getUserById(id: String): UserData? {
        return try {
            val response = client.get<String>("$apiUrl/$id")
            parseUser(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user by id: ${e.message}")
            null
        }
    }

    suspend fun createUser(user: UserData): Boolean {
        return try {
            client.post<Unit>(apiUrl) {
                contentType(ContentType.Application.Json)
                body = user
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user: ${e.message}")
            false
        }
    }

    suspend fun updateUser(user: UserData): Boolean {
        return try {
            client.put<Unit>("$apiUrl/${user.id}") {
                contentType(ContentType.Application.Json)
                body = user
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user: ${e.message}")
            false
        }
    }

    suspend fun deleteUser(id: String): Boolean {
        return try {
            client.delete<Unit>("$apiUrl/$id")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user: ${e.message}")
            false
        }
    }

    private fun parseUsers(response: String): List<UserData> {
        val jsonArray = JSONArray(response)
        val userList = mutableListOf<UserData>()
		
        for (i in 0 until jsonArray.length()) {
            val userJson = jsonArray.getJSONObject(i)
            val userData = parseUser(userJson.toString())
            userData?.let { userList.add(it) }
        }
		
        return userList
    }

    private fun parseUser(response: String): UserData? {
        val jsonObject = JSONObject(response)
        
        val id = jsonObject.getString("_id")
        val name = jsonObject.getString("name")
        val birthDate = jsonObject.getString("birthDate")
        val cpf = jsonObject.getString("cpf")
        val avatarUrl = jsonObject.getString("avatarURL")
		
        return UserData(id, cpf, name, birthDate, avatarUrl)
    }
}
