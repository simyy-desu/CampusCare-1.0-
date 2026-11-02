package com.example.campuscare10.nav

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.campuscare10.datamodel.StaffProfile
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.datamodel.StudentProfiles
import com.example.campuscare10.repository.ReportRepository
import com.example.campuscare10.screen.*
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from

@Composable
fun StaffReportApp() {
    val navController = rememberNavController()
    val reports = remember { mutableStateListOf<StaffReport>() }

    AppNavGraph(navController = navController, reports = reports)
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    reports: SnapshotStateList<StaffReport>
) {
    var currentStaff by remember { mutableStateOf<StaffProfile?>(null) }
    var currentStudent by remember { mutableStateOf<StudentProfiles?>(null) }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToStudent = { navController.navigate("student_login") },
                onNavigateToStaff = { navController.navigate("staff_login") }
            )
        }

        composable("student_login") {
            StudentLoginScreen(
                onLoginSuccess = { student ->
                    currentStudent = student
                    currentStaff = null
                    navController.navigate("student_dashboard") {
                        popUpTo("student_login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegistrationSuccess = {
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("staff_login") {
            StaffLoginScreen(
                onLoginSuccess = { staff ->
                    currentStaff = staff
                    currentStudent = null
                    navController.navigate("staff_dashboard") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("staff_dashboard") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                try {
                    val fetchedReports = supabase.from("reports").select().decodeList<StaffReport>()
                    reports.clear()
                    reports.addAll(fetchedReports)
                    Log.d("Supabase", "Fetched ${fetchedReports.size} reports")
                } catch (e: Exception) {
                    Log.e("Supabase", "Error fetching reports", e)
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            DashboardScreen(reports, navController, currentStaff)
        }

        composable("student_dashboard") {
            StudentDashboardScreen(
                reports = reports,
                navController = navController,
                studentName = currentStudent?.studentName ?: "Student",
                studentId = currentStudent?.studentId ?: ""
            )
        }

        // Added route for Submitting a Report (referenced from student dashboard)
        composable("submit_report") {
            SubmitReportScreen(navController, currentStudent?.studentId ?: "Unknown")
        }

        // Connected Student Profile screen
        composable("student_profile") {
            currentStudent?.let { student ->
                StudentProfileScreen(navController, student, onLogout = { currentStudent = null })
            }
        }

        // Connected Student Notifications / Alerts screen
        composable("student_notifications") {
            StudentNotificationsScreen(navController)
        }

        composable(route = "student_equipment_detail/{equipmentId}") { entry ->
            val equipmentId = entry.arguments?.getString("equipmentId")
            if (equipmentId != null) {
                StudentEquipmentDetailScreen(navController, equipmentId)
            }
        }

        composable("staff_profile") {
            currentStaff?.let { staff ->
                StaffProfileScreen(navController, staff, onLogout = { currentStaff = null })
            }
        }

        composable("staff_reports") {
            StaffReportsScreen(reports, navController)
        }

        composable("reports") {
            MyReportsScreen(navController, currentStudent?.studentId ?: "")
        }

        composable("equipment_list") {
            EquipmentListScreen(navController)
        }

        composable("add_equipment") {
            AddEquipmentScreen(navController)
        }

        composable("equipment_detail/{equipmentId}") { entry ->
            val equipmentId = entry.arguments?.getString("equipmentId")
            if (equipmentId != null) {
                EquipmentDetailScreen(navController, equipmentId)
            }
        }

        composable("edit_equipment/{equipmentId}") { entry ->
            val equipmentId = entry.arguments?.getString("equipmentId")
            if (equipmentId != null) {
                EditEquipmentScreen(navController, equipmentId)
            }
        }

        composable("detail/{reportId}") { entry ->
            val reportId = entry.arguments?.getString("reportId")
            var report by remember { mutableStateOf<StaffReport?>(reports.find { it.id == reportId }) }

            if (report == null && reportId != null) {
                LaunchedEffect(reportId) {
                    report = ReportRepository().getReportById(reportId)
                }
            }

            if (report != null) {
                ReportDetailScreen(
                    report = report!!,
                    navController = navController,
                    isStaff = currentStaff != null
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        composable("update/{reportId}") { entry ->
            val reportId = entry.arguments?.getString("reportId")
            val reportIndex = reports.indexOfFirst { it.id == reportId }

            if (reportIndex >= 0) {
                UpdateStatusScreen(
                    report = reports[reportIndex],
                    onBack = { navController.popBackStack() },
                    onSave = { updatedCategory, newStatus, newRating, newNote, newImageUri ->
                        val updatedReport = reports[reportIndex].copy(
                            category = updatedCategory,
                            status = newStatus,
                            rating = newRating,
                            note = newNote,
                            imageUri = newImageUri
                        )
                        reports[reportIndex] = updatedReport
                        navController.navigate("detail/${updatedReport.id}") {
                            popUpTo("detail/${updatedReport.id}") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}