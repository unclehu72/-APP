package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiOptimizeResult(
    val matchScore: Int,
    val changeHighlights: String,
    val optimizedResume: String,
    val coverLetter: String,
    val interviewTips: String
)
