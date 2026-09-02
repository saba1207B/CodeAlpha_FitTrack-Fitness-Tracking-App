package com.example.fittrack.model

import java.time.LocalDate

data class DayStat(
    val date: LocalDate,
    val dayLabel: String, // "Mon", "Tue", etc.
    val formattedDate: String, // "Sep 2"
    val steps: Int = 0,
    val calories: Int = 0,
    val durationMinutes: Int = 0,
    val workoutCount: Int = 0
)

data class WeeklySummary(
    val totalSteps: Int = 0,
    val totalCalories: Int = 0,
    val totalDurationMinutes: Int = 0,
    val totalWorkouts: Int = 0,
    val averageDailySteps: Int = 0,
    val days: List<DayStat> = emptyList()
)
