package com.demo.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "db_weather")
data class DbWeather(
    @PrimaryKey val id: Int,
    val weatherInfo: String
)