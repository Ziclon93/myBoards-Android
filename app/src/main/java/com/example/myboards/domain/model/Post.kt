package com.example.myboards.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    val id: Int,
    val x: Float,
    val y: Float,
    val rotation: Int,
    val resourceUrl: String,
    val valoration: Float,
    var userLikeCode: Int,
) : Parcelable