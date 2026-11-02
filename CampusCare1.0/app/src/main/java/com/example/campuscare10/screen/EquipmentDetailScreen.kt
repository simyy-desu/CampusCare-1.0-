package com.example.campuscare10.screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import kotlinx.coroutines.launch

@Composable
fun EquipmentDetailScreen(
    navController: NavController,
    equipmentId: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val greenColor = Color(0xFF2E8B57)

    var equipment by remember { mutableStateOf<Equipment?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Equipment") },
            text = { Text("Are you sure you want to delete this equipment? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        coroutineScope.launch {
                            try {
                                supabase.from("equipment").delete {
                                    filter {
                                        eq("id", equipmentId)
                                    }
                                }
                                Toast.makeText(context, "Equipment deleted", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Log.e("Supabase", "Error deleting equipment", e)
                                Toast.makeText(context, "Delete failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Fetch equipment details from Supabase
    LaunchedEffect(equipmentId) {
        try {
            val fetchedEquipment = supabase.from("equipment")
                .select {
                    filter {
                        eq("id", equipmentId)
                    }
                }.decodeSingle<Equipment>()
            equipment = fetchedEquipment
        } catch (e: Exception) {
            Log.e("Supabase", "Error fetching equipment detail", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Equipment Detail",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Delete button
            IconButton(
                onClick = { showDeleteDialog = true }
            ) {

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Equipment",
                    tint = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = greenColor)
            }
        } else if (equipment == null) {
            Text(
                text = "Equipment not found",
                fontSize = 18.sp
            )
        } else {
            // Photo
            if (!equipment?.photoUri.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        Uri.parse(equipment?.photoUri)
                    ),
                    contentDescription = "Equipment Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF3F3F3)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Photo",
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Name
            Text(
                text = equipment?.equipmentName ?: "",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            EquipmentDetailItem(
                title = "Equipment ID",
                value = equipment?.id.toString()
            )

            EquipmentDetailItem(
                title = "Category",
                value = equipment?.category ?: ""
            )

            EquipmentDetailItem(
                title = "Location",
                value = equipment?.location ?: ""
            )

            EquipmentDetailItem(
                title = "Description",
                value = equipment?.description ?: ""
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    navController.navigate("edit_equipment/${equipment?.id}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = greenColor
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Edit Equipment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EquipmentDetailItem(
    title: String,
    value: String
) {

    Column(
        modifier = Modifier.padding(bottom = 14.dp)
    ) {

        Text(
            text = title,
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
