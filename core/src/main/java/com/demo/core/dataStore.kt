package com.demo.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "user_preferences")

class DataStoreManager(private val context: Context) {

    private object PreferencesKeys {
        val CITY_NAME = stringPreferencesKey("city_name")
    }

    // Save city name
    suspend fun saveCity(city: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CITY_NAME] = city
        }
    }

    // Fetch city name directly
    suspend fun getCity(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[PreferencesKeys.CITY_NAME]
    }
}