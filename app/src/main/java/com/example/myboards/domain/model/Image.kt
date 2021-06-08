package com.example.myboards.domain.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    val path: String,
    val bitmap: Bitmap
) : Parcelable