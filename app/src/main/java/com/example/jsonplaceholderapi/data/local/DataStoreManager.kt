package com.example.jsonplaceholderapi.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val NAME_KEY = stringPreferencesKey("user_name")
        val SURNAME_KEY = stringPreferencesKey("user_surname")
        val IMAGE_PATH_KEY = stringPreferencesKey("profile_picture_path")
    }

    suspend fun saveName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = name
        }
    }

    suspend fun saveSurname(surname: String) {
        context.dataStore.edit { preferences ->
            preferences[SURNAME_KEY] = surname
        }
    }

    suspend fun saveImagePath(path: String) {
        context.dataStore.edit { preferences ->
            preferences[IMAGE_PATH_KEY] = path
        }
    }

    suspend fun getName(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[NAME_KEY]
    }

    suspend fun getSurname(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[SURNAME_KEY]
    }

    suspend fun getImagePath(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[IMAGE_PATH_KEY]
    }
}