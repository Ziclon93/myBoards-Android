package com.example.myboards.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Login(
    val apiKey: String
) : Parcelable