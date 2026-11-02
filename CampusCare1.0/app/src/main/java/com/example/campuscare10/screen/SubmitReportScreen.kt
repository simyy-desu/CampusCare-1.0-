package com.example.campuscare10.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.repository.ReportRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitReportScreen(navController: NavController, studentId: String = "Unknown Student") {
    val categoryOptions = listOf("Lecture Hall", "Classroom", "Toilet", "Building", "Others")
    var selectedCategory by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    var descText by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val repo = ReportRepository()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        photoUri = it
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Row(modifier = Modifier.padding(bottom = 16.dp)) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Submit Report", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Text("Category")
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categoryOptions.forEach { cat ->
                    DropdownMenuItem(text = { Text(cat) }, onClick = {
                        selectedCategory = cat
                        expanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Location")
        OutlinedTextField(
            value = locationText,
            onValueChange = { locationText = it },
            placeholder = { Text("e.g. Block A, Room 202") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("Description")
        OutlinedTextField(
            value = descText,
            onValueChange = { if (it.length <= 200) descText = it },
            placeholder = { Text("Describe the issue...") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            supportingText = { Text("${descText.length}/200") }
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("Upload Photo (Optional)")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                .clickable { imagePicker.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = "preview",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(text = "Tap to upload photo", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (selectedCategory.isBlank() || locationText.isBlank() || descText.isBlank()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isSubmitting = true
                scope.launch {
                    try {
                        var finalImageUrl: String? = null

                        if (photoUri != null) {
                            finalImageUrl = repo.uploadImageToStorage(
                                context = context,
                                fileUri = photoUri!!
                            )
                        }

                        val newReport = StaffReport(
                            id = null,
                            category = selectedCategory,
                            location = locationText,
                            description = descText,
                            submittedBy = studentId,
                            status = "Submitted",
                            imageUri = finalImageUrl,
                            note = null
                        )
                        repo.insertNewReport(newReport)
                        
                        Toast.makeText(context, "Report submitted!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        e.printStackTrace()
                        Toast.makeText(context, "Failed to submit report: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        isSubmitting = false
                    }
                }
            },
            enabled = !isSubmitting,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C47FF))
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Submit")
            }
        }
    }
}
