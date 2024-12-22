package com.demo.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
abstract class WeatherDao {

    @Query("SELECT * FROM db_weather")
    abstract fun getWeatherFromDatabase(): DbWeather?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveWeatherToDatabase(posts: DbWeather)
}
