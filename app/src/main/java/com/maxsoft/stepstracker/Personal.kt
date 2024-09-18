package com.maxsoft.stepstracker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "personal_info")

class Personal(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val WEIGHT_KEY = floatPreferencesKey("weight")
        private val HEIGHT_KEY = floatPreferencesKey("height")
        private val AGE_KEY = intPreferencesKey("age")
        private val GENDER_KEY = stringPreferencesKey("gender")
        private val DAILY_GOAL_KEY = intPreferencesKey("daily_goal")
        private val WEEKLY_GOAL_KEY = intPreferencesKey("weekly_goal")
        private val MONTHLY_GOAL_KEY = intPreferencesKey("monthly_goal")
        private val SENSITIVITY_KEY = floatPreferencesKey("sensitivity")
    }

    val weight: Flow<Float> = dataStore.data.map { preferences ->
        preferences[WEIGHT_KEY] ?: 70f
    }

    val height: Flow<Float> = dataStore.data.map { preferences ->
        preferences[HEIGHT_KEY] ?: 170f
    }

    val age: Flow<Int> = dataStore.data.map { preferences ->
        preferences[AGE_KEY] ?: 30
    }

    val gender: Flow<Gender> = dataStore.data.map { preferences ->
        Gender.valueOf(preferences[GENDER_KEY] ?: Gender.MALE.name)
    }

    val dailyGoal: Flow<Int> = dataStore.data.map { preferences ->
        preferences[DAILY_GOAL_KEY] ?: 10000
    }

    val weeklyGoal: Flow<Int> = dataStore.data.map { preferences ->
        preferences[WEEKLY_GOAL_KEY] ?: 70000
    }

    val monthlyGoal: Flow<Int> = dataStore.data.map { preferences ->
        preferences[MONTHLY_GOAL_KEY] ?: 300000
    }

    val sensitivity: Flow<Float> = dataStore.data.map { preferences ->
        preferences[SENSITIVITY_KEY] ?: 1f
    }

    suspend fun updateWeight(weight: Float) {
        dataStore.edit { preferences ->
            preferences[WEIGHT_KEY] = weight
        }
    }

    suspend fun updateHeight(height: Float) {
        dataStore.edit { preferences ->
            preferences[HEIGHT_KEY] = height
        }
    }

    suspend fun updateAge(age: Int) {
        dataStore.edit { preferences ->
            preferences[AGE_KEY] = age
        }
    }

    suspend fun updateGender(gender: Gender) {
        dataStore.edit { preferences ->
            preferences[GENDER_KEY] = gender.name
        }
    }

    suspend fun updateDailyGoal(goal: Int) {
        dataStore.edit { preferences ->
            preferences[DAILY_GOAL_KEY] = goal
        }
    }

    suspend fun updateWeeklyGoal(goal: Int) {
        dataStore.edit { preferences ->
            preferences[WEEKLY_GOAL_KEY] = goal
        }
    }

    suspend fun updateMonthlyGoal(goal: Int) {
        dataStore.edit { preferences ->
            preferences[MONTHLY_GOAL_KEY] = goal
        }
    }

    suspend fun updateSensitivity(sensitivity: Float) {
        dataStore.edit { preferences ->
            preferences[SENSITIVITY_KEY] = sensitivity
        }
    }
}

enum class Gender {
    MALE,
    FEMALE
}