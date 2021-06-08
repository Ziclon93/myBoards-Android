package com.example.myboards.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    @SerialName("username")
    val username: String,
    @SerialName("iconUrl")
    val iconUrl: String,
    @SerialName("valoration")
    val valoration: Float

)