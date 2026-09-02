package com.example.fittrack.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fittrack.data.entity.Workout
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts ORDER BY date DESC, id DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE date >= :startTimestamp AND date <= :endTimestamp ORDER BY date DESC")
    fun getWorkoutsBetweenDates(startTimestamp: Long, endTimestamp: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutById(id: Long): Flow<Workout?>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutByIdOnce(id: Long): Workout?

    @Query("SELECT COALESCE(SUM(caloriesBurned), 0) FROM workouts WHERE date >= :startTimestamp AND date <= :endTimestamp")
    fun getTotalCaloriesBetweenDates(startTimestamp: Long, endTimestamp: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM workouts WHERE date >= :startTimestamp AND date <= :endTimestamp")
    fun getTotalDurationBetweenDates(startTimestamp: Long, endTimestamp: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM workouts WHERE date >= :startTimestamp AND date <= :endTimestamp")
    fun getWorkoutCountBetweenDates(startTimestamp: Long, endTimestamp: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM workouts")
    fun getTotalWorkoutCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<Workout>)

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkoutById(id: Long)

    @Query("DELETE FROM workouts")
    suspend fun deleteAllWorkouts()
}
