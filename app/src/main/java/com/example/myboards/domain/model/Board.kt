package com.example.myboards.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Board(
    val id: Int,
    val title: String,
    val tags: List<String>,
    val iconUrl: String,
    val valoration: Float,
    val postList: List<Post>
) : Parcelable