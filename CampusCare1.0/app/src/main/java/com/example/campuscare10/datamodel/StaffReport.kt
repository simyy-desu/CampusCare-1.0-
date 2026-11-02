package com.example.campuscare10.datamodel

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaffReport(
    @SerialName("id") val id: String? = null,
    val category: String = "",
    val location: String = "",
    val description: String = "",
    @SerialName("submitted_by") val submittedBy: String = "",
    @SerialName("submitted_time") val submittedTime: String? = null,
    val status: String = "Under Review",
    val rating: Float = 0.0f,
    val note: String? = "",
    @SerialName("image_uri") val imageUri: String? = null
)

fun statusColor(status: String): Color {
    return when (status) {
        "In Progress" -> Color(0xFFFF8500)
        "Completed" -> Color(0xFF2E8B28)
        "Under Review" -> Color(0xFF7B43FF)
        "Submitted" -> Color(0xFF4057D6)
        else -> Color(0xFF6B6B6B)
    }
}