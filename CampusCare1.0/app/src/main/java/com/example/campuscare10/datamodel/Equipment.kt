package com.example.campuscare10.datamodel

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Equipment(
    @SerialName("id") val id: String? = null,
    @SerialName("equipment_name") val equipmentName: String,
    @SerialName("category") val category: String,
    @SerialName("location") val location: String,
    @SerialName("description") val description: String,
    @SerialName("photo_uri") val photoUri: String? = null
)
