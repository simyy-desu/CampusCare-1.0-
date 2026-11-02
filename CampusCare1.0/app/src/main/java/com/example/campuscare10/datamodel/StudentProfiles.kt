package com.example.campuscare10.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentProfiles(
    @SerialName("Studentid") val studentId: String? = null,
    @SerialName("StudentName") val studentName: String,
    @SerialName("Email") val email: String,
    @SerialName("Department") val department: String,
    @SerialName("Password") val password: String,
    @SerialName("Rating") val rating: Double,
    @SerialName("PhoneNumber") val phoneNumber: String,
    @SerialName("Studentprofilepic") val studentProfilePic: String? = null
)
