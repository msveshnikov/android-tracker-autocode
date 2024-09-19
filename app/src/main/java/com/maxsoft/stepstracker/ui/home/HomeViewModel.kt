package com.maxsoft.stepstracker.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.maxsoft.stepstracker.Personal
import com.maxsoft.stepstracker.StepData
import com.maxsoft.stepstracker.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val personal = Personal(application)

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _steps = MutableLiveData<Int>()
    val steps: LiveData<Int> = _steps

    private val _calories = MutableLiveData<Float>()
    val calories: LiveData<Float> = _calories

    private val _distance = MutableLiveData<Double>()
    val distance: LiveData<Double> = _distance

    private val _time = MutableLiveData<Long>()
    val time: LiveData<Long> = _time

    private val _isTracking = MutableLiveData<Boolean>()
    val isTracking: LiveData<Boolean> = _isTracking

    private val _dailyGoal = MutableLiveData<Int>()
    val dailyGoal: LiveData<Int> = _dailyGoal

    private val _sensitivity = MutableLiveData<Float>()
    val sensitivity: LiveData<Float> = _sensitivity

    private val _lastDayStats = MutableLiveData<List<StepData>>()
    val lastDayStats: LiveData<List<StepData>> = _lastDayStats

    private val _weeklyStats = MutableLiveData<List<StepData>>()
    val weeklyStats: LiveData<List<StepData>> = _weeklyStats

    private val _monthlyStats = MutableLiveData<List<StepData>>()
    val monthlyStats: LiveData<List<StepData>> = _monthlyStats

    init {
        viewModelScope.launch {
            personal.dailyGoal.collect { _dailyGoal.value = it }
        }
        viewModelScope.launch {
            personal.sensitivity.collect { _sensitivity.value = it }
        }
        loadMockData()
    }

    fun toggleTracking() {
        _isTracking.value = _isTracking.value?.not() ?: true
    }

    fun resetSteps() {
        _steps.value = 0
        _calories.value = 0f
        _distance.value = 0.0
        _time.value = 0L
    }

    fun updateSteps(newSteps: Int) {
        _steps.value = newSteps
        _calories.value = calculateCalories(newSteps)
        _distance.value = calculateDistance(newSteps)
        _time.value = System.currentTimeMillis()
        updateUiState()
    }

    private fun calculateCalories(steps: Int): Float {
        return steps * 0.05f
    }

    private fun calculateDistance(steps: Int): Double {
        return steps * 0.0008
    }

    fun updateDailyGoal(goal: Int) {
        viewModelScope.launch {
            personal.updateDailyGoal(goal)
        }
    }

    fun updateSensitivity(sensitivity: Float) {
        viewModelScope.launch {
            personal.updateSensitivity(sensitivity)
        }
    }

    private fun updateUiState() {
        _uiState.value = UiState.Success(
            steps = _steps.value ?: 0,
            calories = _calories.value ?: 0f,
            distance = _distance.value ?: 0.0,
            time = _time.value ?: 0L,
            lastDayStats = _lastDayStats.value ?: emptyList(),
            weeklyStats = _weeklyStats.value ?: emptyList(),
            monthlyStats = _monthlyStats.value ?: emptyList(),
            isTracking = _isTracking.value ?: false,
            sensitivity = _sensitivity.value ?: 1f,
            dailyGoal = _dailyGoal.value ?: 10000,
            userWeight = 70f,
            userHeight = 170f,
            userAge = 30,
            userGender = com.maxsoft.stepstracker.Gender.MALE,
            weeklyGoal = 70000,
            monthlyGoal = 300000
        )
    }

    private fun loadMockData() {
        val now = LocalDateTime.now()
        _lastDayStats.value = List(24) { index ->
            StepData(
                timestamp = now.minusHours(index.toLong()),
                steps = (100..1000).random(),
                calories = (10..100).random().toFloat(),
                distance = 0.5f
            )
        }
        _weeklyStats.value = List(7) { index ->
            StepData(
                timestamp = now.minusDays(index.toLong()),
                steps = (1000..10000).random(),
                calories = (100..1000).random().toFloat(),
                distance =  0.5f
            )
        }
        _monthlyStats.value = List(30) { index ->
            StepData(
                timestamp = now.minusDays(index.toLong()),
                steps = (5000..20000).random(),
                calories = (500..2000).random().toFloat(),
                distance = 0.5f
            )
        }
    }
}