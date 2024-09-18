package com.maxsoft.stepstracker

sealed interface UiState {
    object Initial : UiState
    object Loading : UiState
    data class Success(
        val steps: Int,
        val calories: Float,
        val distance: Float,
        val time: Long,
        val lastDayStats: List<StepData>,
        val weeklyStats: List<StepData>,
        val monthlyStats: List<StepData>
    ) : UiState
    data class Error(val message: String) : UiState
}

data class StepData(
    val timestamp: Long,
    val steps: Int,
    val calories: Float,
    val distance: Float
)