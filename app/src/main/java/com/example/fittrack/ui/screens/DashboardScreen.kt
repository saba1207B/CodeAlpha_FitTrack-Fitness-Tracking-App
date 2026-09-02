package com.example.fittrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fittrack.data.entity.Workout
import com.example.fittrack.ui.components.DeleteConfirmDialog
import com.example.fittrack.ui.components.MetricCard
import com.example.fittrack.ui.components.StepProgressRingCard
import com.example.fittrack.ui.components.UpdateStepsDialog
import com.example.fittrack.ui.components.WorkoutCard
import com.example.fittrack.viewmodel.FitTrackViewModel
import com.example.ui.theme.CalorieAccentColor
import com.example.ui.theme.StepAccentColor
import com.example.ui.theme.TimeAccentColor
import com.example.ui.theme.WorkoutAccentColor
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: FitTrackViewModel,
    onNavigateToAddWorkout: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.dashboardUiState.collectAsStateWithLifecycle()
    val deleteCandidate by viewModel.deleteCandidate.collectAsStateWithLifecycle()
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    if (uiState.isStepDialogOpen) {
        UpdateStepsDialog(
            currentSteps = uiState.todaySteps,
            onDismiss = { viewModel.setStepDialogOpen(false) },
            onConfirm = { steps -> viewModel.updateTodaySteps(steps) }
        )
    }

    if (deleteCandidate != null) {
        DeleteConfirmDialog(
            title = "Delete Workout?",
            message = "Are you sure you want to remove this ${deleteCandidate?.exerciseType} workout record?",
            onDismiss = { viewModel.dismissDeleteDialog() },
            onConfirm = { viewModel.confirmDeleteWorkout() }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Good day!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.currentDateFormatted,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Today's Fitness Summary 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Steps",
                        value = numberFormat.format(uiState.todaySteps),
                        subtitle = "Goal: ${numberFormat.format(uiState.stepGoal)}",
                        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                        accentColor = StepAccentColor,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_steps_card"
                    )

                    MetricCard(
                        title = "Calories",
                        value = numberFormat.format(uiState.todayCalories),
                        unit = "kcal",
                        subtitle = "Burned today",
                        icon = Icons.Default.LocalFireDepartment,
                        accentColor = CalorieAccentColor,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_calories_card"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Workout",
                        value = "${uiState.todayDurationMinutes}",
                        unit = "min",
                        subtitle = "Active time",
                        icon = Icons.Default.Timer,
                        accentColor = TimeAccentColor,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_duration_card"
                    )

                    MetricCard(
                        title = "Workouts",
                        value = "${uiState.todayWorkoutCount}",
                        subtitle = "Sessions logged",
                        icon = Icons.Default.FitnessCenter,
                        accentColor = WorkoutAccentColor,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_workout_count_card"
                    )
                }
            }
        }

        // Daily Step Tracker Card with Circular Progress
        item {
            StepProgressRingCard(
                currentSteps = uiState.todaySteps,
                stepGoal = uiState.stepGoal,
                onUpdateStepsClick = { viewModel.setStepDialogOpen(true) }
            )
        }

        // Today's Workouts Header & Quick Action
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Workouts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (uiState.todayWorkouts.isNotEmpty()) {
                    TextButton(onClick = onNavigateToHistory) {
                        Text("View All")
                    }
                }
            }
        }

        // Today's Workouts Items
        if (uiState.todayWorkouts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No workouts logged today",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Keep moving! Log your exercise to track burned calories.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToAddWorkout,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("Log Workout")
                        }
                    }
                }
            }
        } else {
            items(uiState.todayWorkouts, key = { it.id }) { workout ->
                WorkoutCard(
                    workout = workout,
                    onEditClick = {
                        viewModel.loadWorkoutForEdit(workout)
                        onNavigateToAddWorkout()
                    },
                    onDeleteClick = {
                        viewModel.requestDeleteWorkout(workout)
                    }
                )
            }
        }
    }
}
