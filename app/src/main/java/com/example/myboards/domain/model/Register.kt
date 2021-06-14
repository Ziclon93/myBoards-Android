package com.example.myboards.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Register(
    val apiKey: String
) : Parcelable