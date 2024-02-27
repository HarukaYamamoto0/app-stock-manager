package com.harukadev.stockmanager.data

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
val id: String,
val cpf: String,
val name: String,
val birthDate: String,
val avatarURL: String = "https://imgur.com/YqECnW7.png"
)