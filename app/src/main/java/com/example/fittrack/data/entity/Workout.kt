package com.example.fittrack.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val exerciseType: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val date: Long, // timestamp in milliseconds
    val notes: String = ""
)
