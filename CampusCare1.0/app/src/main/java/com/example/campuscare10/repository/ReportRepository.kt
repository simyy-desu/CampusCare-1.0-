package com.example.campuscare10.repository

import android.content.Context
import android.net.Uri
import com.example.campuscare10.datamodel.StaffReport
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.UUID
import java.util.Locale
import com.example.campuscare10.supabase.supabase

class ReportRepository {

    suspend fun fetchAllReports(): List<StaffReport> {
        return try {
            supabase
                .from("reports")
                .select()
                .decodeList<StaffReport>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getReportById(reportId: String): StaffReport? {
        return try {
            val list = supabase
                .from("reports")
                .select {
                    filter {
                        eq("id", reportId)
                    }
                }
                .decodeList<StaffReport>()
            list.firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun insertNewReport(newReport: StaffReport) {
        supabase
            .from("reports")
            .insert(newReport)
    }

    suspend fun updateStudentRating(studentId: String) {
        try {
            val reports = fetchAllReports()
            val studentReports = reports.filter { it.submittedBy == studentId && it.rating != null }
            val averageRating = if (studentReports.isNotEmpty()) {
                val total = studentReports.sumOf { it.rating?.toDouble() ?: 0.0 }
                total / studentReports.size
            } else {
                0.0
            }
            val formattedRating = String.format(Locale.getDefault(), "%.1f", averageRating).toDouble()

            supabase.from("Studentprofiles").update({
                set("Rating", formattedRating)
            }) {
                filter { eq("Studentid", studentId) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun uploadImageToStorage(
        context: Context,
        fileUri: Uri,
        bucketName: String = "report_photo"
    ): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(fileUri)
            val bytes = inputStream?.readBytes()

            if (bytes == null) return@withContext null

            val fileName = "${UUID.randomUUID()}.jpg"

            supabase.storage.from(bucketName).upload(fileName, bytes)
            val publicUrl = supabase.storage.from(bucketName).publicUrl(fileName)
            return@withContext publicUrl

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}