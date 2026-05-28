package com.example.data.dao

import androidx.room.*
import com.example.data.entity.ResumeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resume_table WHERE id = 1 LIMIT 1")
    fun getResumeFlow(): Flow<ResumeEntity?>

    @Query("SELECT * FROM resume_table WHERE id = 1 LIMIT 1")
    suspend fun getResume(): ResumeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(resume: ResumeEntity)
}
