package com.example.fittrack.data.repository

import com.example.fittrack.data.dao.DailyStepDao
import com.example.fittrack.data.dao.WorkoutDao
import com.example.fittrack.data.entity.DailyStep
import com.example.fittrack.data.entity.Workout
import com.example.fittrack.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class FitTrackRepository(
    private val workoutDao: WorkoutDao,
    private val dailyStepDao: DailyStepDao,
    private val preferencesRepository: UserPreferencesRepository
) {

    // Preferences
    val stepGoal: Flow<Int> = preferencesRepository.dailyStepGoal

    suspend fun setStepGoal(goal: Int) {
        preferencesRepository.setDailyStepGoal(goal)
    }

    // Workouts
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkouts()

    fun getWorkoutsBetweenDates(startTimestamp: Long, endTimestamp: Long): Flow<List<Workout>> {
        return workoutDao.getWorkoutsBetweenDates(startTimestamp, endTimestamp)
    }

    fun getWorkoutById(id: Long): Flow<Workout?> {
        return workoutDao.getWorkoutById(id)
    }

    suspend fun getWorkoutByIdOnce(id: Long): Workout? {
        return workoutDao.getWorkoutByIdOnce(id)
    }

    suspend fun insertWorkout(workout: Workout): Long {
        return workoutDao.insertWorkout(workout)
    }

    suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout)
    }

    suspend fun deleteWorkout(workout: Workout) {
        workoutDao.deleteWorkout(workout)
    }

    suspend fun deleteWorkoutById(id: Long) {
        workoutDao.deleteWorkoutById(id)
    }

    // Steps
    fun getStepsForDate(dateEpochDay: Long): Flow<DailyStep?> {
        return dailyStepDao.getStepsForDate(dateEpochDay)
    }

    suspend fun getStepsForDateOnce(dateEpochDay: Long): DailyStep? {
        return dailyStepDao.getStepsForDateOnce(dateEpochDay)
    }

    fun getStepsBetweenDays(startEpochDay: Long, endEpochDay: Long): Flow<List<DailyStep>> {
        return dailyStepDao.getStepsBetweenDays(startEpochDay, endEpochDay)
    }

    suspend fun updateSteps(dateEpochDay: Long, steps: Int) {
        dailyStepDao.insertOrUpdateStep(
            DailyStep(
                dateEpochDay = dateEpochDay,
                steps = steps,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    // Sample data insertion (debug/testing)
    suspend fun insertSampleWorkouts(workouts: List<Workout>, steps: List<DailyStep>) {
        workoutDao.insertWorkouts(workouts)
        dailyStepDao.insertOrUpdateSteps(steps)
    }

    // Reset all data
    suspend fun resetAllData() {
        workoutDao.deleteAllWorkouts()
        dailyStepDao.deleteAllSteps()
        preferencesRepository.clearPreferences()
    }
}
