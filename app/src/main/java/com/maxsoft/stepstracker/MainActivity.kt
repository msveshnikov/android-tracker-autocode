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
import java.time.LocalDateTime
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
            viewModel::resetSteps,
            viewModel::updateUserInfo,
            viewModel::updateDailyGoal,
            viewModel::updateSensitivity,
            viewModel::backupData,
            viewModel::restoreData,
            viewModel::shareProgress,
            viewModel::syncWithWearable,
            viewModel::joinChallenge
        )

        is UiState.Error -> ErrorScreen(state.message)
        UiState.Initial, UiState.Loading -> LoadingScreen()
    }
}

@Composable
fun StepsTrackerContent(
    state: UiState.Success,
    onToggleTracking: () -> Unit,
    onResetSteps: () -> Unit,
    onUpdateUserInfo: (Float, Float, Int, Gender) -> Unit,
    onUpdateDailyGoal: (Int) -> Unit,
    onUpdateSensitivity: (Float) -> Unit,
    onBackupData: () -> Unit,
    onRestoreData: () -> Unit,
    onShareProgress: () -> Unit,
    onSyncWithWearable: () -> Unit,
    onJoinChallenge: () -> Unit
) {
    var showUserInfoDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showSensitivityDialog by remember { mutableStateOf(false) }

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

        Button(onClick = { showUserInfoDialog = true }) {
            Text(stringResource(R.string.personal_info_settings))
        }

        Button(onClick = { showGoalDialog = true }) {
            Text(stringResource(R.string.goal_daily))
        }

        Button(onClick = { showSensitivityDialog = true }) {
            Text(stringResource(R.string.settings_sensitivity))
        }

        Button(onClick = onBackupData) {
            Text(stringResource(R.string.settings_backup))
        }

        Button(onClick = onRestoreData) {
            Text(stringResource(R.string.settings_restore))
        }

        Button(onClick = onShareProgress) {
            Text(stringResource(R.string.share_progress))
        }

        Button(onClick = onSyncWithWearable) {
            Text(stringResource(R.string.sync_wearable))
        }

        Button(onClick = onJoinChallenge) {
            Text(stringResource(R.string.challenge_join))
        }

        LazyColumn {
            item {
                Text(
                    text = stringResource(R.string.graph_last_24_hours),
                    style = MaterialTheme.typography.titleMedium
                )
                StepChart(data = state.lastDayStats)
            }
            item {
                Text(
                    text = stringResource(R.string.graph_weekly),
                    style = MaterialTheme.typography.titleMedium
                )
                StepChart(data = state.weeklyStats)
            }
            item {
                Text(
                    text = stringResource(R.string.graph_monthly),
                    style = MaterialTheme.typography.titleMedium
                )
                StepChart(data = state.monthlyStats)
            }
        }
    }

    if (showUserInfoDialog) {
        UserInfoDialog(
            currentWeight = state.userWeight,
            currentHeight = state.userHeight,
            currentAge = state.userAge,
            currentGender = state.userGender,
            onDismiss = { showUserInfoDialog = false },
            onConfirm = { weight, height, age, gender ->
                onUpdateUserInfo(weight, height, age, gender)
                showUserInfoDialog = false
            }
        )
    }

    if (showGoalDialog) {
        GoalDialog(
            currentGoal = state.dailyGoal,
            onDismiss = { showGoalDialog = false },
            onConfirm = { goal ->
                onUpdateDailyGoal(goal)
                showGoalDialog = false
            }
        )
    }

    if (showSensitivityDialog) {
        SensitivityDialog(
            currentSensitivity = state.sensitivity,
            onDismiss = { showSensitivityDialog = false },
            onConfirm = { sensitivity ->
                onUpdateSensitivity(sensitivity)
                showSensitivityDialog = false
            }
        )
    }
}

@Composable
fun StepChart(data: List<StepData>) {
}

@Composable
fun UserInfoDialog(
    currentWeight: Float,
    currentHeight: Float,
    currentAge: Int,
    currentGender: Gender,
    onDismiss: () -> Unit,
    onConfirm: (Float, Float, Int, Gender) -> Unit
) {
    var weight by remember { mutableStateOf(currentWeight) }
    var height by remember { mutableStateOf(currentHeight) }
    var age by remember { mutableStateOf(currentAge) }
    var gender by remember { mutableStateOf(currentGender) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.personal_info_settings)) },
        text = {
            Column {
                OutlinedTextField(
                    value = weight.toString(),
                    onValueChange = { weight = it.toFloatOrNull() ?: weight },
                    label = { Text(stringResource(R.string.weight_setting)) }
                )
                OutlinedTextField(
                    value = height.toString(),
                    onValueChange = { height = it.toFloatOrNull() ?: height },
                    label = { Text(stringResource(R.string.height_setting)) }
                )
                OutlinedTextField(
                    value = age.toString(),
                    onValueChange = { age = it.toIntOrNull() ?: age },
                    label = { Text(stringResource(R.string.age_setting)) }
                )
                Row {
                    RadioButton(
                        selected = gender == Gender.MALE,
                        onClick = { gender = Gender.MALE }
                    )
                    Text(stringResource(R.string.gender_male))
                    RadioButton(
                        selected = gender == Gender.FEMALE,
                        onClick = { gender = Gender.FEMALE }
                    )
                    Text(stringResource(R.string.gender_female))
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(weight, height, age, gender) }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}

@Composable
fun GoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var goal by remember { mutableStateOf(currentGoal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.goal_daily)) },
        text = {
            OutlinedTextField(
                value = goal.toString(),
                onValueChange = { goal = it.toIntOrNull() ?: goal },
                label = { Text(stringResource(R.string.goal_daily)) }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(goal) }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}

@Composable
fun SensitivityDialog(
    currentSensitivity: Int,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var sensitivity by remember { mutableStateOf(currentSensitivity) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_sensitivity)) },
        text = {
            Slider(
                value = sensitivity.toFloat(),
                onValueChange = { sensitivity = it.toInt() },
                valueRange = 0.5f..2f,
                steps = 15
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(sensitivity.toFloat()) }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
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
    private var userWeight = 70f
    private var userHeight = 170f
    private var userAge = 30
    private var userGender = Gender.MALE
    private var dailyGoal = 10000
    private var weeklyGoal = 70000
    private var monthlyGoal = 300000
    private var sensitivity = 1f

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
                sensitivity = 10,
                dailyGoal = dailyGoal,
                userWeight = userWeight,
                userHeight = userHeight,
                userAge = userAge,
                userGender = userGender,
                weeklyGoal = weeklyGoal,
                monthlyGoal = monthlyGoal
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
            val stepsTaken = ((currentSteps - lastStepCount) * sensitivity).toInt()
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
                userGender = userGender,
                sensitivity = 10,
                lastDayStats = generateMockStepData(24),
                weeklyStats = generateMockStepData(7),
                monthlyStats = generateMockStepData(30)
            )
        }
    }

    private fun calculateCalories(steps: Int): Float {
        val strideLengthInMeters = (userHeight * 0.415) / 100
        val distanceInKm = (steps * strideLengthInMeters) / 1000
        val speed =
            distanceInKm / (TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - startTime) + 1)
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

    fun updateUserInfo(weight: Float, height: Float, age: Int, gender: Gender) {
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

    fun updateSensitivity(newSensitivity: Float) {
        sensitivity = newSensitivity
        updateUiState()
    }

    private fun generateMockStepData(count: Int): List<StepData> {
        val now = LocalDateTime.now()
        return List(count) { index ->
            StepData(
                timestamp = now.minusDays(index.toLong()),
                steps = (1000..10000).random(),
                calories = (100..1000).random().toFloat(),
                distance = (1..10).random().toFloat()
            )
        }
    }

    fun backupData() {
    }

    fun restoreData() {
    }

    fun shareProgress() {
    }

    fun syncWithWearable() {
    }

    fun joinChallenge() {
    }
}

