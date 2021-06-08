package com.example.myboards.data.model.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PostBoardRequest(
    @SerializedName("title") var title: String?,
    @SerializedName("tags") var tags: List<String>?,
    @SerializedName("iconUrl") var iconUrl: String?
)