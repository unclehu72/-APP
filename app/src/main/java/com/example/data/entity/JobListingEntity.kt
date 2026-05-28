package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_listings")
data class JobListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyName: String,
    val title: String,
    val salary: String,
    val location: String,
    val description: String,
    val source: String = "Boss直聘",
    val matchScore: Int = 0,
    val scannedTimestamp: Long = System.currentTimeMillis()
)
