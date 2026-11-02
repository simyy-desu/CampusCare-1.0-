package com.example.campuscare10.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaffProfile(
    @SerialName("Staffid") val staffId: String,
    val password: String,
    @SerialName("Department") val department: String? = null,
    val email: String? = null,
    @SerialName("PhoneNumber") val phoneNumber: String? = null,
    @SerialName("StaffName") val staffName: String? = null,
    @SerialName("Staffprofilepic") val staffProfilePic: String? = null
)
