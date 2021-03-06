package com.example.myboards.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TagBoards(
    val tagName: String,
    val boardList: List<Board>,
) : Parcelable