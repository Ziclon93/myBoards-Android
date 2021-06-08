package com.example.myboards.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Profile(
    val userName: String,
    val iconUrl: String,
    val valoration: Float,
) : Parcelable