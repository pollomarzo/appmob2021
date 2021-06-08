package com.example.pantryfin.ui.login

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class LoginResponse(
    val accessToken: String
)

@Serializable
data class BrowseResponse(
    val products: List<BrowseItems>,
    val token: String,
)

@Serializable
data class BrowseItems(
    val id:String,
    val name: String,
    val description: String,
    val barcode: String,
    val img: String?,
    val userId: String,
    val test: Boolean,
    val createdAt: String,
    val updatedAt: String,
)