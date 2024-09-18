package com.maxsoft.stepstracker.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxsoft.stepstracker.Personal
import com.maxsoft.stepstracker.StepData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class DashboardViewModel(private val personal: Personal) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _dailySteps = MutableLiveData<Int>()
    val dailySteps: LiveData<Int> = _dailySteps

    private val _weeklySteps = MutableLiveData<Int>()
    val weeklySteps: LiveData<Int> = _weeklySteps

    private val _monthlySteps = MutableLiveData<Int>()
    val monthlySteps: LiveData<Int> = _monthlySteps

    private val _caloriesBurned = MutableLiveData<Float>()
    val caloriesBurned: LiveData<Float> = _caloriesBurned

    private val _distance = MutableLiveData<Float>()
    val distance: LiveData<Float> = _distance

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            try {
                val dailyGoal = personal.dailyGoal.value
                val weight = personal.weight.value
                val height = personal.height.value

                val mockStepData = generateMockStepData(30)
                val dailySteps = mockStepData.first().steps
                val weeklySteps = mockStepData.take(7).sumOf { it.steps }
                val monthlySteps = mockStepData.sumOf { it.steps }

                _dailySteps.value = dailySteps
                _weeklySteps.value = weeklySteps
                _monthlySteps.value = monthlySteps
                _caloriesBurned.value = calculateCaloriesBurned(dailySteps, weight)
                _distance.value = calculateDistance(dailySteps, height)

                _uiState.value = DashboardUiState.Success(
                    dailySteps = dailySteps,
                    weeklySteps = weeklySteps,
                    monthlySteps = monthlySteps,
                    dailyGoal = dailyGoal,
                    caloriesBurned = _caloriesBurned.value ?: 0f,
                    distance = _distance.value ?: 0f,
                    dailyStepData = mockStepData.take(24),
                    weeklyStepData = mockStepData.take(7),
                    monthlyStepData = mockStepData
                )
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error("Failed to load dashboard data")
            }
        }
    }

    private fun generateMockStepData(days: Int): List<StepData> {
        val now = LocalDateTime.now()
        return List(days) { index ->
            StepData(
                timestamp = now.minusDays(index.toLong()),
                steps = (1000..10000).random(),
                calories = (100..1000).random().toFloat(),
                distance = (1..10).random().toFloat()
            )
        }
    }

    private fun calculateCaloriesBurned(steps: Int, weight: Float): Float {
        return steps * 0.04f * weight
    }

    private fun calculateDistance(steps: Int, height: Float): Float {
        val strideLength = height * 0.415f
        return steps * strideLength / 100000f
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(
        val dailySteps: Int,
        val weeklySteps: Int,
        val monthlySteps: Int,
        val dailyGoal: Int,
        val caloriesBurned: Float,
        val distance: Float,
        val dailyStepData: List<StepData>,
        val weeklyStepData: List<StepData>,
        val monthlyStepData: List<StepData>
    ) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}