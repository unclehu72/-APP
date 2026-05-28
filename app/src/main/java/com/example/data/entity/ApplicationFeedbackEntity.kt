package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "application_feedbacks")
data class ApplicationFeedbackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyName: String,
    val jobTitle: String,
    val salary: String,
    val location: String,
    val appliedTimestamp: Long = System.currentTimeMillis(),
    val matchScore: Int,
    val status: String, // e.g., "已投递", "简历备选", "邀约面试", "初筛通过"
    val optimizedResume: String,
    val changeHighlights: String,
    val coverLetter: String,
    val interviewTips: String
)
