package com.example.myboards.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileIconUrlResponse(
    @SerialName("iconUrl")
    val iconUrl: String
)