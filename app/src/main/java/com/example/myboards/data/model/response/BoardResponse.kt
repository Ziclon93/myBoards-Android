package com.example.myboards.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoardResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("iconUrl")
    val iconUrl: String,
    @SerialName("valoration")
    val valoration: Float,
    @SerialName("postList")
    val postList: List<PostResponse>
)