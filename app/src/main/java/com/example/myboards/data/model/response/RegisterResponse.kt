package com.example.myboards.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    @SerialName("apiKey")
    val apiKey: String
)