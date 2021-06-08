package com.example.myboards.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("x")
    val x: Float,
    @SerialName("y")
    val y: Float,
    @SerialName("rotation")
    val rotation: Int,
    @SerialName("resourceUrl")
    val resourceUrl: String,
    @SerialName("valoration")
    val valoration: Float
)