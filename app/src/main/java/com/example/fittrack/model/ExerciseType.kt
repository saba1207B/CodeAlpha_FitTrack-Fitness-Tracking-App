package com.example.fittrack.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExerciseType(
    val displayName: String,
    val defaultCaloriesPerMinute: Double,
    val icon: ImageVector,
    val color: Color
) {
    WALKING("Walking", 4.5, Icons.AutoMirrored.Filled.DirectionsWalk, Color(0xFF10B981)),
    RUNNING("Running", 10.0, Icons.AutoMirrored.Filled.DirectionsRun, Color(0xFFEF4444)),
    CYCLING("Cycling", 8.0, Icons.AutoMirrored.Filled.DirectionsBike, Color(0xFFF59E0B)),
    SWIMMING("Swimming", 9.0, Icons.Default.Pool, Color(0xFF06B6D4)),
    GYM("Gym", 6.0, Icons.Default.FitnessCenter, Color(0xFF8B5CF6)),
    STRENGTH_TRAINING("Strength Training", 6.5, Icons.Default.SportsGymnastics, Color(0xFFEC4899)),
    YOGA("Yoga", 3.5, Icons.Default.SelfImprovement, Color(0xFF14B8A6)),
    CARDIO("Cardio", 8.5, Icons.Default.VolunteerActivism, Color(0xFFFF6D00)),
    OTHER("Other", 5.0, Icons.Default.MoreHoriz, Color(0xFF64748B));

    companion object {
        fun fromString(name: String): ExerciseType {
            return entries.find { it.displayName.equals(name, ignoreCase = true) || it.name.equals(name, ignoreCase = true) } ?: OTHER
        }

        val allNames: List<String> = entries.map { it.displayName }
    }
}
