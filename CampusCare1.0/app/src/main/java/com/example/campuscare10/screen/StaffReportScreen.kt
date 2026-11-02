package com.example.campuscare10.screen

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.campuscare10.datamodel.StaffProfile
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.datamodel.statusColor
import coil.compose.AsyncImage

@Composable
fun DashboardScreen(reports: List<StaffReport>, navController: NavController, staff: StaffProfile?) {
    val inProgress = reports.count { it.status == "In Progress" }
    val completed = reports.count { it.status == "Completed" }

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
                    onClick = { navController.navigate("staff_reports") },
                    icon = { Text("▤") },
                    label = { Text("Reports") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("equipment_list") },
                    icon = { Text("☰") },
                    label = { Text("Equipment") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("staff_profile") },
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
                .padding(20.dp)
        ) {
            Text(
                "Hi, ${staff?.staffName ?: "Staff"}!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text("Here's the overview today.", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(20.dp))

            Text("Overview", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OverviewCard(
                    "Total Reports",
                    reports.size.toString(),
                    Color(0xFFE2E4FF),
                    Modifier.weight(1f)
                )
                OverviewCard(
                    "In Progress",
                    inProgress.toString(),
                    Color(0xFFFFE9D7),
                    Modifier.weight(1f)
                )
                OverviewCard(
                    "Completed",
                    completed.toString(),
                    Color(0xFFDDEFD9),
                    Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text("Recent Reports", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sortedReports = reports.sortedByDescending { it.id?.toIntOrNull() ?: 0 }
                items(sortedReports) { report ->
                    ReportCard(report) {
                        navController.navigate("detail/${report.id}")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("add_equipment") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("+ Add New Equipment")
            }
        }
    }
}

@Composable
fun OverviewCard(title: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun StaffReportsScreen(reports: List<StaffReport>, navController: NavController) {
    var selectedStatus by remember { mutableStateOf("All Status") }
    val statuses = listOf("All Status", "Submitted", "Under Review", "In Progress", "Completed")

    val filteredReports = reports.filter {
        selectedStatus == "All Status" || it.status == selectedStatus
    }.sortedByDescending { it.id?.toIntOrNull() ?: 0 }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(false, { navController.navigate("staff_dashboard") }, { Text("⌂") }, label = { Text("Home") })
                NavigationBarItem(true, {}, { Text("▤") }, label = { Text("Reports") })
                NavigationBarItem(false, { navController.navigate("equipment_list") }, { Text("☰") }, label = { Text("Equipment") })
                NavigationBarItem(false, { navController.navigate("staff_profile") }, { Text("◉") }, label = { Text("Profile") })
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(padding)
        ) {
            Text("All Reports", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(12.dp))
            StatusDropdown(selectedStatus, statuses) { selectedStatus = it }

            Spacer(Modifier.height(14.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filteredReports) { report ->
                    ReportCard(report) {
                        navController.navigate("detail/${report.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(report: StaffReport, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        border = BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(report.category, fontWeight = FontWeight.Bold)
            Text(report.location, style = MaterialTheme.typography.bodySmall)
            Text(
                "Status: ${report.status}",
                style = MaterialTheme.typography.bodySmall,
                color = statusColor(report.status),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusDropdown(
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}