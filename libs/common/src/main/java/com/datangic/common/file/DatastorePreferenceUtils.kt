package com.datangic.common.file

import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.datangic.common.Config.DATASTORE_NAME
import kotlinx.coroutines.flow.catch
import java.io.IOException

private val Context.dataStore by preferencesDataStore(
    name = DATASTORE_NAME,
    produceMigrations = { context ->
        // Since we're migrating from SharedPreferences, add a migration based on the
        // SharedPreferences name
        listOf(SharedPreferencesMigration(context, DATASTORE_NAME))
    }
)

object DatastorePreferenceUtils {

    suspend fun <T : Any> storeValue(context: Context, key: String, value: T) {
        context.dataStore.edit { preferences ->
            when (value) {
                is String -> preferences[stringPreferencesKey(key)] = value as String
                is Boolean -> preferences[booleanPreferencesKey(key)] = value as Boolean
                is Int -> preferences[intPreferencesKey(key)] = value as Int
                is Double -> preferences[doublePreferencesKey(key)] = value as Double
                is Long -> preferences[longPreferencesKey(key)] = value as Long
                is Float -> preferences[floatPreferencesKey(key)] = value as Float
            }
        }
    }

    suspend fun <T : Any> getValue(context: Context, key: String, defaultValue: T): T {
        var result: Any? = null
        context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.collect { preferences ->
            result = when (defaultValue) {
                is String -> preferences[stringPreferencesKey(key)]
                is Boolean -> preferences[booleanPreferencesKey(key)]
                is Int -> preferences[intPreferencesKey(key)]
                is Double -> preferences[doublePreferencesKey(key)]
                is Long -> preferences[longPreferencesKey(key)]
                is Float -> preferences[floatPreferencesKey(key)]
                else -> defaultValue
            }
        }
        return result?.let { return it as T } ?: defaultValue
    }
}
