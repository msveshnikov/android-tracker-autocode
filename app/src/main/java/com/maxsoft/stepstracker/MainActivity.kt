package com.maxsoft.stepstracker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maxsoft.stepstracker.ui.theme.StepsTrackerTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StepsTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StepsTrackerApp()
                }
            }
        }
    }
}

@Composable
fun StepsTrackerApp(viewModel: StepsTrackerViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initializeSensor(context)
    }

    when (val state = uiState) {
        is UiState.Success -> StepsTrackerContent(
            state,
            viewModel::toggleTracking,
            viewModel::resetSteps
        )
        is UiState.Error -> ErrorScreen(state.message)
        UiState.Initial, UiState.Loading -> LoadingScreen()
    }
}

@Composable
fun StepsTrackerContent(
    state: UiState.Success,
    onToggleTracking: () -> Unit,
    onResetSteps: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.steps_count, state.steps),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(R.string.calories_burned, state.calories),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(R.string.distance_walked, state.distance),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(
                R.string.time_elapsed,
                String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(state.time),
                    TimeUnit.MILLISECONDS.toMinutes(state.time) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(state.time) % 60
                )
            ),
            style = MaterialTheme.typography.bodyLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onToggleTracking) {
                Text(
                    if (state.isTracking) stringResource(R.string.action_pause) else stringResource(
                        R.string.action_start
                    )
                )
            }
            Button(onClick = onResetSteps) {
                Text(stringResource(R.string.action_reset))
            }
        }

        LazyColumn {
            item {
                Text(
                    text = stringResource(R.string.graph_last_24_hours),
                    style = MaterialTheme.typography.titleMedium
                )
                // TODO: Implement chart for last 24 hours
            }
            item {
                Text(
                    text = stringResource(R.string.graph_weekly),
                    style = MaterialTheme.typography.titleMedium
                )
                // TODO: Implement weekly chart
            }
            item {
                Text(
                    text = stringResource(R.string.graph_monthly),
                    style = MaterialTheme.typography.titleMedium
                )
                // TODO: Implement monthly chart
            }
        }
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

class StepsTrackerViewModel : ViewModel(), SensorEventListener {
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private var isTracking = false
    private var lastStepCount = 0
    private var startTime = 0L
    private var userWeight = 70f // Default weight in kg
    private var userHeight = 170f // Default height in cm
    private var userAge = 30 // Default age
    private var userGender = "male" // Default gender
    private var dailyGoal = 10000 // Default daily goal

    fun initializeSensor(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            _uiState.value = UiState.Error(context.getString(R.string.error_no_sensor))
        } else {
            _uiState.value = UiState.Success(
                steps = 0,
                calories = 0f,
                distance = 0.0,
                time = 0L,
                lastDayStats = emptyList(),
                weeklyStats = emptyList(),
                monthlyStats = emptyList(),
                isTracking = false,
                sensitivity = 1,
                dailyGoal = dailyGoal,
                userWeight = userWeight,
                userHeight = userHeight,
                userAge = userAge,
                userGender = Gender.MALE,
                weeklyGoal = 30000,
                monthlyGoal = 120000
            )
        }
    }

    fun toggleTracking() {
        isTracking = !isTracking
        if (isTracking) {
            startTracking()
        } else {
            stopTracking()
        }
        updateUiState()
    }

    private fun startTracking() {
        sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        startTime = System.currentTimeMillis()
    }

    private fun stopTracking() {
        sensorManager?.unregisterListener(this)
    }

    fun resetSteps() {
        lastStepCount = 0
        startTime = System.currentTimeMillis()
        updateUiState()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (isTracking && event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val currentSteps = event.values[0].toInt()
            if (lastStepCount == 0) {
                lastStepCount = currentSteps
            }
            val stepsTaken = currentSteps - lastStepCount
            updateUiState(stepsTaken)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updateUiState(steps: Int = 0) {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            _uiState.value = currentState.copy(
                steps = steps,
                calories = calculateCalories(steps),
                distance = calculateDistance(steps),
                time = System.currentTimeMillis() - startTime,
                isTracking = isTracking,
                dailyGoal = dailyGoal,
                userWeight = userWeight,
                userHeight = userHeight,
                userAge = userAge,
                userGender = Gender.MALE
            )
        }
    }

    private fun calculateCalories(steps: Int): Float {
        val strideLengthInMeters = (userHeight * 0.415) / 100
        val distanceInKm = (steps * strideLengthInMeters) / 1000
        val speed = distanceInKm / (TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - startTime) + 1)
        val met = when {
            speed < 4.0 -> 2.9
            speed < 5.6 -> 3.3
            speed < 7.2 -> 3.8
            else -> 5.0
        }
        return (met * 3.5 * userWeight * (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startTime) + 1) / 200).toFloat()
    }

    private fun calculateDistance(steps: Int): Double {
        val strideLengthInMeters = (userHeight * 0.415) / 100
        return (steps * strideLengthInMeters) / 1000
    }

    fun updateUserInfo(weight: Float, height: Float, age: Int, gender: String) {
        userWeight = weight
        userHeight = height
        userAge = age
        userGender = gender
        updateUiState()
    }

    fun updateDailyGoal(goal: Int) {
        dailyGoal = goal
        updateUiState()
    }
}