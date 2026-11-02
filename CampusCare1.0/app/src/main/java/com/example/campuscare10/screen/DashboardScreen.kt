package com.example.campuscare10.screen // Adjust package path to match your project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.datamodel.StudentProfiles
import com.example.campuscare10.repository.ReportRepository

@Composable
fun StudentDashboardScreen(
    reports: List<StaffReport>,
    navController: NavController,
    studentName: String = "Student",
    studentId: String = ""
) {
    val repo = ReportRepository()
    var reportList by remember { mutableStateOf<List<StaffReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(reports) {
        isLoading = true
        // Fetch from database to get up-to-date and all records for this user
        val allReports = repo.fetchAllReports()
        // Filter by current student id and sort descending by id so latest reports appear first
        reportList = allReports.filter { it.submittedBy == studentId }
            .sortedByDescending { it.id?.toIntOrNull() ?: 0 }
        isLoading = false
    }

    val total = reportList.size
    val inProgress = reportList.count { it.status == "In Progress" }
    val completed = reportList.count { it.status == "Completed" }



    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("⌂") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("reports") {
                            popUpTo("student_dashboard") { inclusive = false }
                        }
                    },
                    icon = { Text("▤") },
                    label = { Text("Reports") }
                )
                // Navigation to the Alerts/Notifications screen tab
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("student_notifications") {
                            popUpTo("student_dashboard") { inclusive = false }
                        }
                    },
                    icon = { Text("☰") },
                    label = { Text("Equipment") }
                )
                // Navigation to the Student Profile screen tab
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("student_profile") {
                            popUpTo("student_dashboard") { inclusive = false }
                        }
                    },
                    icon = { Text("◉") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Text("Hi, $studentName!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("How can we help today?", color = Color.Gray)
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("submit_report") },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF303F9F))
            ) {
                Text("+ Submit New Report", color = Color.White)
            }

            Spacer(Modifier.height(20.dp))
            Text("Overview", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard("$total", "Total Reports", Color(0xFFE2E4FF))
                StatCard("$inProgress", "In Progress", Color(0xFFFFE8D6))
                StatCard("$completed", "Completed", Color(0xFFD9F2E4))
            }

            Spacer(Modifier.height(20.dp))
            Text("Recent Reports", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))

            if (isLoading) {
                Text(text = "Loading reports...", modifier = Modifier.fillMaxWidth())
            } else if (reportList.isEmpty()) {
                Text(text = "No reports found.", color = Color.Gray, modifier = Modifier.fillMaxWidth())
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = reportList,
                        key = { it.id ?: "" }
                    ) { report ->
                        ReportRow(report = report, onClick = {
                            navController.navigate("detail/${report.id}")
                        })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, bg: Color) {
    Column(
        modifier = Modifier
            .width(105.dp)
            .height(105.dp)
            .background(bg, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = when (label) {
                "Total Reports" -> Color(0xFF303F9F)
                "In Progress" -> Color(0xFFFF7700)
                "Completed" -> Color(0xFF009944)
                else -> Color.Gray
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = when (label) {
                "Total Reports" -> Color(0xFF303F9F)
                "In Progress" -> Color(0xFFFF7700)
                "Completed" -> Color(0xFF009944)
                else -> Color.Gray
            }
        )
    }
}

@Composable
fun ReportRow(report: StaffReport, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text("Category: ${report.category}", fontWeight = FontWeight.Medium)
        Text("Location: ${report.location}", color = Color.DarkGray)
        val statusColor = when (report.status) {
            "Submitted" -> Color(0xFF303F9F)
            "In Progress" -> Color(0xFFFF7700)
            "Completed" -> Color(0xFF009944)
            else -> Color.Gray
        }
        Text("Status: ${report.status}", color = statusColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}