package com.example.campuscare10.screen

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.datamodel.statusColor

@Composable
fun ReportDetailScreen(
    report: StaffReport,
    navController: NavController,
    isStaff: Boolean
) {
    Scaffold(
        bottomBar = {
            if (isStaff) {
                Box(modifier = Modifier.padding(20.dp)) {
                    Button(
                        onClick = { navController.navigate("update/${report.id}") },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Update Status", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "‹",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(end = 22.dp)
                        .clickable { navController.popBackStack() }
                )
                Text("Report Detail", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(18.dp))
            AssistChip(
                onClick = {},
                label = { Text(report.status) },
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = statusColor(report.status)
                )
            )

            Spacer(Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (!report.imageUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = Uri.parse(report.imageUri),
                            contentDescription = "Uploaded Report Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("No Photo Uploaded")
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Text("Report Detail", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            DetailItem("Location", report.location)
            DetailItem("Description", report.description)
            DetailItem("Submitted by", report.submittedBy)
            DetailItem("Submitted time", report.submittedTime ?: "")
            DetailItem("Report ID", "#${report.id}")
            DetailItem("Category", report.category)

            if (!report.note.isNullOrBlank()) {
                DetailItem("Admin Note", report.note)
            }
            DetailItem("Rating", "${report.rating} / 5.0")
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
