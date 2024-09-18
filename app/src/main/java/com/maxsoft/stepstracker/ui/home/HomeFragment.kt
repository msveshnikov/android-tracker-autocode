package com.maxsoft.stepstracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.maxsoft.stepstracker.R
import com.maxsoft.stepstracker.StepsTrackerViewModel
import com.maxsoft.stepstracker.UiState
import com.maxsoft.stepstracker.ui.theme.StepsTrackerTheme

class HomeFragment : Fragment() {

    private lateinit var viewModel: StepsTrackerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[StepsTrackerViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                StepsTrackerTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        HomeScreen(viewModel)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initializeSensor(requireContext())
    }
}

@Composable
fun HomeScreen(viewModel: StepsTrackerViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is UiState.Success -> HomeContent(
            state,
            viewModel::toggleTracking,
            viewModel::resetSteps
        )
        is UiState.Error -> ErrorContent(state.message)
        UiState.Initial, UiState.Loading -> LoadingContent()
    }
}

@Composable
fun HomeContent(
    state: UiState.Success,
    onToggleTracking: () -> Unit,
    onResetSteps: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
            text = stringResource(R.string.time_elapsed, formatTime(state.time)),
            style = MaterialTheme.typography.bodyLarge
        )

        Button(onClick = onToggleTracking) {
            Text(
                if (state.isTracking) stringResource(R.string.action_pause)
                else stringResource(R.string.action_start)
            )
        }
        Button(onClick = onResetSteps) {
            Text(stringResource(R.string.action_reset))
        }

        Text(
            text = stringResource(R.string.graph_last_24_hours),
            style = MaterialTheme.typography.titleMedium
        )
        DailyGraph(data = state.lastDayStats)

        Text(
            text = stringResource(R.string.graph_weekly),
            style = MaterialTheme.typography.titleMedium
        )
        WeeklyGraph(data = state.weeklyStats)

        Text(
            text = stringResource(R.string.graph_monthly),
            style = MaterialTheme.typography.titleMedium
        )
        MonthlyGraph(data = state.monthlyStats)
    }
}

@Composable
fun ErrorContent(message: String) {
    Text(text = message, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun LoadingContent() {
    Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
}

private fun formatTime(timeInMillis: Long): String {
    val hours = timeInMillis / 3600000
    val minutes = (timeInMillis % 3600000) / 60000
    val seconds = (timeInMillis % 60000) / 1000
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}