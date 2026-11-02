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
fun EditEquipmentScreen(
    navController: NavController,
    equipmentId: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val greenColor = Color(0xFF2E8B57)

    var equipmentName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    // Fetch initial data
    LaunchedEffect(equipmentId) {
        try {
            val fetchedEquipment = supabase.from("equipment")
                .select {
                    filter {
                        eq("id", equipmentId)
                    }
                }.decodeSingle<Equipment>()
            
            equipmentName = fetchedEquipment.equipmentName
            selectedCategory = fetchedEquipment.category
            location = fetchedEquipment.location
            description = fetchedEquipment.description
            photoUri = fetchedEquipment.photoUri?.let { Uri.parse(it) }
            
        } catch (e: Exception) {
            Log.e("Supabase", "Error fetching equipment for edit", e)
            Toast.makeText(context, "Error loading equipment: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            isLoading = false
        }
    }

    val categoryOptions = listOf(
        "Classroom Equipment",
        "Electrical Equipment",
        "Lab Equipment",
        "Furniture",
        "Others"
    )

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        if (it != null) photoUri = it
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    if (equipmentName.isNotBlank() && selectedCategory.isNotBlank() && location.isNotBlank()) {
                        coroutineScope.launch {
                            isSaving = true
                            try {
                                var finalPhotoUrl = photoUri?.toString()

                                // Upload if it's a new local URI
                                if (photoUri != null && photoUri.toString().startsWith("content://")) {
                                    val bytes = withContext(Dispatchers.IO) {
                                        context.contentResolver.openInputStream(photoUri!!)?.use { it.readBytes() }
                                    }
                                    if (bytes != null) {
                                        val fileName = "equip_${System.currentTimeMillis()}.jpg"
                                        val bucket = supabase.storage.from("equipment_photos")
                                        bucket.upload(fileName, bytes)
                                        finalPhotoUrl = bucket.publicUrl(fileName)
                                    }
                                }

                                val updateData = Equipment(
                                    equipmentName = equipmentName,
                                    category = selectedCategory,
                                    location = location,
                                    description = description,
                                    photoUri = finalPhotoUrl
                                )

                                supabase.from("equipment").update(updateData) {
                                    filter {
                                        eq("id", equipmentId)
                                    }
                                }

                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Updated successfully!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            } catch (e: Exception) {
                                Log.e("Supabase", "Error updating equipment", e)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            } finally {
                                isSaving = false
                            }
                        }
                    }
                },
                enabled = !isSaving && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isSaving) "Saving..." else "Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = greenColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = "Edit Equipment",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Equipment Name", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = equipmentName,
                    onValueChange = { equipmentName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
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

                Text(text = "Location", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Description", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length <= 200) description = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    supportingText = { Text("${description.length}/200") },
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Equipment Photo", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
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
                }
            }
        }
    }
}
