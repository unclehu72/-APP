package com.example.data.dao

import androidx.room.*
import com.example.data.entity.ApplicationFeedbackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationFeedbackDao {
    @Query("SELECT * FROM application_feedbacks ORDER BY appliedTimestamp DESC")
    fun getAllFeedbacksFlow(): Flow<List<ApplicationFeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: ApplicationFeedbackEntity): Long

    @Update
    suspend fun updateFeedback(feedback: ApplicationFeedbackEntity)

    @Query("DELETE FROM application_feedbacks")
    suspend fun deleteAllFeedbacks()

    @Query("DELETE FROM application_feedbacks WHERE id = :id")
    suspend fun deleteFeedbackById(id: Int)
}
