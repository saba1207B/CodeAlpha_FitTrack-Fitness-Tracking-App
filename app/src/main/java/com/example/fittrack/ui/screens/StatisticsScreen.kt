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
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import com.example.fittrack.model.DayStat
import com.example.fittrack.ui.components.ChartType
import com.example.fittrack.ui.components.MetricCard
import com.example.fittrack.ui.components.WeeklyBarChart
import com.example.fittrack.viewmodel.FitTrackViewModel
import com.example.ui.theme.CalorieAccentColor
import com.example.ui.theme.StepAccentColor
import com.example.ui.theme.TimeAccentColor
import com.example.ui.theme.WorkoutAccentColor
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: FitTrackViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.statisticsUiState.collectAsStateWithLifecycle()
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    val tabs = listOf(
        "Calories" to ChartType.Calories,
        "Duration" to ChartType.Duration,
        "Steps" to ChartType.Steps
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("statistics_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "Weekly Statistics",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Summary for the past 7 days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 4 Weekly Summary Cards (2x2 Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Weekly Steps",
                        value = numberFormat.format(uiState.weeklySummary.totalSteps),
                        subtitle = "Avg: ${numberFormat.format(uiState.weeklySummary.averageDailySteps)}/day",
                        icon = Icons.Default.DirectionsWalk,
                        accentColor = StepAccentColor,
                        modifier = Modifier.weight(1f),
                        testTag = "weekly_steps_card"
                    )

                    MetricCard(
                        title = "Weekly Calories",
                        value = numberFormat.format(uiState.weeklySummary.totalCalories),
                        unit = "kcal",
                        subtitle = "7-day total",
                        icon = Icons.Default.LocalFireDepartment,
                        accentColor = CalorieAccentColor,
                        modifier = Modifier.weight(1f),
                        testTag = "weekly_calories_card"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Total Time",
                        value = "${uiState.weeklySummary.totalDurationMinutes}",
                        unit = "min",
                        subtitle = "Active exercise",
                        icon = Icons.Default.Timer,
                        accentColor = TimeAccentColor,
                        modifier = Modifier.weight(1f),
                        testTag = "weekly_duration_card"
                    )

                    MetricCard(
                        title = "Total Workouts",
                        value = "${uiState.weeklySummary.totalWorkouts}",
                        subtitle = "Sessions completed",
                        icon = Icons.Default.FitnessCenter,
                        accentColor = WorkoutAccentColor,
                        modifier = Modifier.weight(1f),
                        testTag = "weekly_workout_count_card"
                    )
                }
            }
        }

        // Chart Category Tabs
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                PrimaryTabRow(
                    selectedTabIndex = uiState.selectedChartTab,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, (label, _) ->
                        Tab(
                            selected = uiState.selectedChartTab == index,
                            onClick = { viewModel.setSelectedStatsTab(index) },
                            text = {
                                Text(
                                    text = label,
                                    fontWeight = if (uiState.selectedChartTab == index) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("stats_tab_$index")
                        )
                    }
                }
            }
        }

        // Active Chart
        item {
            val currentChartType = tabs[uiState.selectedChartTab].second
            WeeklyBarChart(
                dayStats = uiState.weeklySummary.days,
                chartType = currentChartType
            )
        }

        // Day-by-Day Breakdown Header
        item {
            Text(
                text = "Daily Breakdown",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Day Breakdown Items
        items(uiState.weeklySummary.days.reversed(), key = { it.date.toEpochDay() }) { day ->
            DayBreakdownRow(day = day)
        }
    }
}

@Composable
private fun DayBreakdownRow(
    day: DayStat,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day and date
            Column {
                Text(
                    text = day.dayLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = day.formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Stats pill group
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Steps
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${numberFormat.format(day.steps)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = StepAccentColor
                    )
                    Text(
                        text = "steps",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Calories
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${numberFormat.format(day.calories)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = CalorieAccentColor
                    )
                    Text(
                        text = "kcal",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Duration
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${day.durationMinutes}m",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TimeAccentColor
                    )
                    Text(
                        text = "${day.workoutCount} wkt",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
