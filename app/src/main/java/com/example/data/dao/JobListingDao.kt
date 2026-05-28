package com.example.data.dao

import androidx.room.*
import com.example.data.entity.JobListingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobListingDao {
    @Query("SELECT * FROM job_listings ORDER BY scannedTimestamp DESC")
    fun getAllJobsFlow(): Flow<List<JobListingEntity>>

    @Query("SELECT * FROM job_listings ORDER BY scannedTimestamp DESC")
    suspend fun getAllJobs(): List<JobListingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobListingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobListingEntity): Long

    @Query("DELETE FROM job_listings")
    suspend fun deleteAllJobs()

    @Delete
    suspend fun deleteJob(job: JobListingEntity)
}
