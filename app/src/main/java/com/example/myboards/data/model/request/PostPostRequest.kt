package com.example.myboards.data.model.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PostPostRequest(
    @SerializedName("boardId") var title: Int?,
    @SerializedName("resourceUrl") var resourceUrl: String?,
)