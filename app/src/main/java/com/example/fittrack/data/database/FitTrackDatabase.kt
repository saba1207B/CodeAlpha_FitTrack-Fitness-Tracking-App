package com.example.fittrack.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fittrack.data.dao.DailyStepDao
import com.example.fittrack.data.dao.WorkoutDao
import com.example.fittrack.data.entity.DailyStep
import com.example.fittrack.data.entity.Workout

@Database(
    entities = [Workout::class, DailyStep::class],
    version = 1,
    exportSchema = false
)
abstract class FitTrackDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun dailyStepDao(): DailyStepDao

    companion object {
        @Volatile
        private var INSTANCE: FitTrackDatabase? = null

        fun getDatabase(context: Context): FitTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitTrackDatabase::class.java,
                    "fittrack_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
