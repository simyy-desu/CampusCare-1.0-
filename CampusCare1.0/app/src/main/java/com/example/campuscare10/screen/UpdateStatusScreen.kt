package com.example.campuscare10.screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import coil.compose.AsyncImage

import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Serializable
data class ReportUpdate(
    val category: String,
    val status: String,
    val rating: Float,
    val note: String?,
    @SerialName("image_uri") val imageUri: String?
)

@Composable
fun UpdateStatusScreen(
    report: StaffReport,
    onBack: () -> Unit,
    onSave: (String, String, Float, String?, String?) -> Unit
) {
    var category by remember { mutableStateOf(report.category) }
    var selectedStatus by remember { mutableStateOf(report.status) }
    var rating by remember { mutableFloatStateOf(report.rating) }
    var note by remember { mutableStateOf(report.note ?: "") }
    var imageUri by remember { mutableStateOf(report.imageUri) }
    var isUploading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri.toString()
        }
    }

    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.padding(20.dp)) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isUploading = true
                            try {
                                var finalImageUri = imageUri
                                
                                // If it's a local URI (picked from gallery), upload it to Supabase
                                if (imageUri != null && imageUri!!.startsWith("content://")) {
                                    val uri = Uri.parse(imageUri)
                                    val bytes = withContext(Dispatchers.IO) {
                                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                                    }
                                    
                                    if (bytes != null) {
                                        val fileName = "report_${report.id}_${System.currentTimeMillis()}.jpg"
                                        val bucket = supabase.storage.from("report_photo")
                                        
                                        Log.d("Supabase", "Uploading image: $fileName")
                                        bucket.upload(fileName, bytes, upsert = true)
                                        
                                        finalImageUri = bucket.publicUrl(fileName)
                                        Log.d("Supabase", "Image uploaded successfully. Public URL: $finalImageUri")
                                    }
                                }

                                val updateData = ReportUpdate(
                                    category = category,
                                    status = selectedStatus,
                                    rating = rating,
                                    note = note.ifBlank { null },
                                    imageUri = finalImageUri
                                )

                                Log.d("Supabase", "Attempting to update report ${report.id} with data: $updateData")
                                supabase.from("reports").update(updateData) {
                                    filter {
                                        eq("id", report.id ?: "")
                                    }
                                }
                                Log.d("Supabase", "Successfully updated report ${report.id} in Supabase")
                                
                                // Show success toast on main thread
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Update successful!", Toast.LENGTH_SHORT).show()
                                    onSave(category, selectedStatus, rating, note, finalImageUri)
                                }
                            } catch (e: Exception) {
                                Log.e("Supabase", "Error updating report ${report.id}", e)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            } finally {
                                isUploading = false
                            }
                        }
                    },
                    enabled = !isUploading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
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
                        .clickable { onBack() }
                )
                Text("Update Status", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(18.dp))
            Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            StatusDropdown(
                category, listOf(
                    "Broken Equipment", "Electrical Issue", "Cleaning Request", "Furniture Damage"
                )
            ) { category = it }

            Spacer(Modifier.height(18.dp))
            Text("Upload Photo", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable { launcher.launch("image/*") },
                border = BorderStroke(1.dp, Color(0xFFE5E5E5))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (!imageUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = Uri.parse(imageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("Tap to upload photo")
                    }
                    if (isUploading) {
                        CircularProgressIndicator()
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Text("Update to", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    listOf("Under Review", "In Progress", "Completed").forEach { status ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStatus = status }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedStatus == status,
                                onClick = { selectedStatus = status }
                            )
                            Text(status)
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Text("Rate Student Report", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (i in 1..5) {
                    Text(
                        text = if (i <= rating) "★" else "☆",
                        color = Color(0xFFFFC107),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .clickable { rating = i.toFloat() }
                            .padding(end = 4.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(String.format("%.1f", rating), fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(18.dp))
            Text("Note (Optional)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { if (it.length <= 200) note = it },
                placeholder = { Text("Add internal note...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            )
            Text(
                text = "${note.length}/200",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}
