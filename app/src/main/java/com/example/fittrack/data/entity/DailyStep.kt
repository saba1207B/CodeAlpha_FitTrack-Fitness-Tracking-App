package com.example.fittrack.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_steps")
data class DailyStep(
    @PrimaryKey
    val dateEpochDay: Long, // LocalDate.toEpochDay()
    val steps: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
