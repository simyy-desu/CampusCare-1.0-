package com.example.campuscare10.screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.campuscare10.datamodel.Equipment
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEquipmentScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val greenColor = Color(0xFF2E8B57)

    val categoryOptions = listOf(
        "Classroom Equipment",
        "Electrical Equipment",
        "Lab Equipment",
        "Furniture",
        "Others"
    )

    var equipmentName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        photoUri = it
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Add New Equipment",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Equipment Name
        Text(text = "Equipment Name", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = equipmentName,
            onValueChange = { equipmentName = it },
            placeholder = { Text("e.g. Projector") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category
        Text(text = "Category", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categoryOptions.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Location
        Text(text = "Location", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            placeholder = { Text("e.g. Block A, Room 202") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(text = "Description", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { if (it.length <= 200) description = it },
            placeholder = { Text("Describe the equipment...") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            supportingText = { Text("${description.length}/200") },
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Photo
        Text(text = "Equipment Photo", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF3F3F3))
                .clickable { imagePicker.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = "Equipment Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(35.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Tap to upload photo", color = Color.Gray)
                }
            }
            if (isSaving) {
                CircularProgressIndicator(color = greenColor)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save
        Button(
            onClick = {
                if (equipmentName.isNotBlank() && selectedCategory.isNotBlank() && location.isNotBlank()) {
                    coroutineScope.launch {
                        isSaving = true
                        try {
                            var uploadedPhotoUrl: String? = null

                            // 1. Upload Photo if exists
                            if (photoUri != null) {
                                val bytes = withContext(Dispatchers.IO) {
                                    context.contentResolver.openInputStream(photoUri!!)?.use { it.readBytes() }
                                }
                                if (bytes != null) {
                                    val fileName = "equip_${System.currentTimeMillis()}.jpg"
                                    val bucket = supabase.storage.from("equipment_photos")
                                    bucket.upload(fileName, bytes)
                                    uploadedPhotoUrl = bucket.publicUrl(fileName)
                                }
                            }

                            // 2. Save to Supabase
                            val equipment = Equipment(
                                equipmentName = equipmentName,
                                category = selectedCategory,
                                location = location,
                                description = description,
                                photoUri = uploadedPhotoUrl
                            )

                            supabase.from("equipment").insert(equipment)

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Equipment added successfully!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        } catch (e: Exception) {
                            Log.e("Supabase", "Error adding equipment", e)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        } finally {
                            isSaving = false
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = greenColor),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = if (isSaving) "Saving..." else "Save",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
