package com.maxsoft.stepstracker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime

@Composable
fun Graph(data: List<StepData>, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val width = size.width
        val height = size.height
        val maxSteps = data.maxOfOrNull { it.steps } ?: 0
        val stepWidth = width / (data.size - 1)

        val path = Path()
        data.forEachIndexed { index, stepData ->
            val x = index * stepWidth
            val y = height - (stepData.steps.toFloat() / maxSteps * height)
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 3f)
        )

        data.forEachIndexed { index, stepData ->
            val x = index * stepWidth
            val y = height - (stepData.steps.toFloat() / maxSteps * height)
            drawCircle(
                color = Color.Red,
                radius = 5f,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun DailyGraph(data: List<StepData>, modifier: Modifier = Modifier) {
    Graph(data = data.takeLast(24), modifier = modifier)
}

@Composable
fun WeeklyGraph(data: List<StepData>, modifier: Modifier = Modifier) {
    Graph(data = data.takeLast(7), modifier = modifier)
}

@Composable
fun MonthlyGraph(data: List<StepData>, modifier: Modifier = Modifier) {
    Graph(data = data.takeLast(30), modifier = modifier)
}

@Composable
fun CaloriesGraph(data: List<StepData>, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val width = size.width
        val height = size.height
        val maxCalories = data.maxOfOrNull { it.calories } ?: 0f
        val stepWidth = width / (data.size - 1)

        val path = Path()
        data.forEachIndexed { index, stepData ->
            val x = index * stepWidth
            val y = height - (stepData.calories / maxCalories * height)
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = Color.Green,
            style = Stroke(width = 3f)
        )

        data.forEachIndexed { index, stepData ->
            val x = index * stepWidth
            val y = height - (stepData.calories / maxCalories * height)
            drawCircle(
                color = Color.Yellow,
                radius = 5f,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun DistanceGraph(data: List<StepData>, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val width = size.width
        val height = size.height
        val maxDistance = data.maxOfOrNull { it.distance } ?: 0f
        val stepWidth = width / (data.size - 1)

        val path = Path()
        data.forEachIndexed { index, stepData ->
            val x = index * stepWidth
            val y = height - (stepData.distance / maxDistance * height)
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = Color.Magenta,
            style = Stroke(width = 3f)
        )

        data.forEachIndexed { index, stepData ->
            val x = index * stepWidth
            val y = height - (stepData.distance / maxDistance * height)
            drawCircle(
                color = Color.Cyan,
                radius = 5f,
                center = Offset(x, y)
            )
        }
    }
}

fun generateMockStepData(count: Int): List<StepData> {
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