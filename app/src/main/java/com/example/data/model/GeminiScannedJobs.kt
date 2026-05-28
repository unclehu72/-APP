package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScannedJob(
    val companyName: String,
    val title: String,
    val salary: String,
    val location: String,
    val description: String
)

@JsonClass(generateAdapter = true)
data class GeminiScannedJobs(
    val jobs: List<ScannedJob>
)
