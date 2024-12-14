package com.imagetool.bgremover.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("app_preferences")

object DataStoreHelper {
    const val USER_IMAGE_COUNT_SHARED_KEY = "user_image_count_key"


    suspend fun saveInt(key: String, value: Int, context: Context) {
        val preferenceKey = intPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferenceKey] = value
        }
    }


    fun getInt(key: String, defaultValue: Int = 0, context: Context): Flow<Int> {
        val preferenceKey = intPreferencesKey(key)
        return context.dataStore.data
            .catch { e ->
                e.printStackTrace()
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[preferenceKey] ?: defaultValue
            }
    }
}