package com.example

import com.example.fittrack.data.entity.Workout
import com.example.fittrack.model.ExerciseType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun exerciseType_calorieEstimations_areValid() {
        val runningType = ExerciseType.fromString("Running")
        assertEquals(ExerciseType.RUNNING, runningType)

        val durationMinutes = 30
        val estimatedRunningCalories = (durationMinutes * runningType.defaultCaloriesPerMinute).toInt()
        assertEquals(300, estimatedRunningCalories)

        val walkingType = ExerciseType.fromString("Walking")
        val estimatedWalkingCalories = (durationMinutes * walkingType.defaultCaloriesPerMinute).toInt()
        assertEquals(135, estimatedWalkingCalories)
    }

    @Test
    fun workoutEntity_instantiation_isCorrect() {
        val workout = Workout(
            id = 1L,
            exerciseType = "Cycling",
            durationMinutes = 45,
            caloriesBurned = 360,
            date = 1725278400000L,
            notes = "Morning ride"
        )
        assertEquals(1L, workout.id)
        assertEquals("Cycling", workout.exerciseType)
        assertEquals(45, workout.durationMinutes)
        assertEquals(360, workout.caloriesBurned)
        assertEquals("Morning ride", workout.notes)
    }
}

