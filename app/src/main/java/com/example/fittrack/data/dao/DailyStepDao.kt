package com.example.fittrack.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittrack.data.entity.DailyStep
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStepDao {

    @Query("SELECT * FROM daily_steps WHERE dateEpochDay = :dateEpochDay")
    fun getStepsForDate(dateEpochDay: Long): Flow<DailyStep?>

    @Query("SELECT * FROM daily_steps WHERE dateEpochDay = :dateEpochDay")
    suspend fun getStepsForDateOnce(dateEpochDay: Long): DailyStep?

    @Query("SELECT * FROM daily_steps WHERE dateEpochDay >= :startEpochDay AND dateEpochDay <= :endEpochDay ORDER BY dateEpochDay ASC")
    fun getStepsBetweenDays(startEpochDay: Long, endEpochDay: Long): Flow<List<DailyStep>>

    @Query("SELECT * FROM daily_steps ORDER BY dateEpochDay DESC")
    fun getAllSteps(): Flow<List<DailyStep>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStep(dailyStep: DailyStep)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSteps(dailySteps: List<DailyStep>)

    @Query("DELETE FROM daily_steps")
    suspend fun deleteAllSteps()
}
