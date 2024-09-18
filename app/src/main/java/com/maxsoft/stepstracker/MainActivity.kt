package com.maxsoft.stepstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.maxsoft.stepstracker.ui.theme.StepsTrackerTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

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
    val steps = remember { mutableStateOf(0) }
    val isTracking = remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Steps: ${steps.value}")
        Button(
            onClick = {
                if (isTracking.value) {
                    viewModel.stopTracking()
                    isTracking.value = false
                } else {
                    viewModel.startTracking()
                    isTracking.value = true
                }
            }
        ) {
            Text(if (isTracking.value) "Stop Tracking" else "Start Tracking")
        }
        Button(onClick = { viewModel.resetSteps() }) {
            Text("Reset Steps")
        }
    }
}

class StepsTrackerViewModel : androidx.lifecycle.ViewModel() {
    fun startTracking() {
        // Implement step tracking logic
    }

    fun stopTracking() {
        // Implement logic to stop tracking
    }

    fun resetSteps() {
        // Implement logic to reset step count
    }
}