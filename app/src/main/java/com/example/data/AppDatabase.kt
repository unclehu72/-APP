package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ApplicationFeedbackDao
import com.example.data.dao.JobListingDao
import com.example.data.dao.ResumeDao
import com.example.data.entity.ApplicationFeedbackEntity
import com.example.data.entity.JobListingEntity
import com.example.data.entity.ResumeEntity

@Database(
    entities = [
        ResumeEntity::class,
        JobListingEntity::class,
        ApplicationFeedbackEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun resumeDao(): ResumeDao
    abstract fun jobListingDao(): JobListingDao
    abstract fun applicationFeedbackDao(): ApplicationFeedbackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "haitou_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
