package com.maxsoft.stepstracker

import java.time.LocalDateTime

sealed interface UiState {
    object Initial : UiState
    object Loading : UiState
    data class Success(
        val steps: Int,
        val calories: Float,
        val distance: Double,
        val time: Long,
        val lastDayStats: List<StepData>,
        val weeklyStats: List<StepData>,
        val monthlyStats: List<StepData>,
        val isTracking: Boolean,
        val sensitivity: Int,
        val dailyGoal: Int,
        val weeklyGoal: Int,
        val monthlyGoal: Int,
        val userWeight: Float,
        val userHeight: Float,
        val userAge: Int,
        val userGender: Gender
    ) : UiState
    data class Error(val message: String) : UiState
}

data class StepData(
    val timestamp: LocalDateTime,
    val steps: Int,
    val calories: Float,
    val distance: Float
)

enum class Gender {
    MALE, FEMALE, OTHER
}

data class UserPreferences(
    val weight: Float,
    val height: Float,
    val age: Int,
    val gender: Gender,
    val sensitivity: Int,
    val dailyGoal: Int,
    val weeklyGoal: Int,
    val monthlyGoal: Int,
    val language: String
)

data class ActivityBreakdown(
    val walking: Int,
    val running: Int,
    val other: Int
)

data class Challenge(
    val id: String,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val goal: Int,
    val participants: List<String>
)