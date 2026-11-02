package com.example.campuscare10.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val id: String? = null,
    val created_at: String? = null,
    val category: String,
    val location: String,
    val description: String,
    @SerialName("submitted_time")
    val submittedTime: String? = null,
    @SerialName("submitted_by")
    val submittedBy: String?,
    val status: String,
    val rating: Double? = null,
    val note: String? = null,
    @SerialName("image_uri")
    val imageUri: String?
)
