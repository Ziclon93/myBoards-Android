package com.example.myboards.data.model.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileIconUrlRequest(
    @SerializedName("iconUrl") var iconUrl: String?,
)