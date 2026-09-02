package com.example.fittrack.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fittrack_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val STEP_GOAL = intPreferencesKey("daily_step_goal")
    }

    val dailyStepGoal: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.STEP_GOAL] ?: 10000
        }

    suspend fun setDailyStepGoal(goal: Int) {
        try {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.STEP_GOAL] = goal
            }
        } catch (_: Exception) {}
    }

    suspend fun clearPreferences() {
        try {
            context.dataStore.edit { preferences ->
                preferences.clear()
            }
        } catch (_: Exception) {}
    }
}
