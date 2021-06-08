package com.example.myboards.data.model.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    @SerializedName("email") var email: String?,
    @SerializedName("username") var username: String?,
    @SerializedName("password") var password: String?
)