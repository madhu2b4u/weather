package com.demo.core.weather.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val country: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val region: String,
) : Parcelable