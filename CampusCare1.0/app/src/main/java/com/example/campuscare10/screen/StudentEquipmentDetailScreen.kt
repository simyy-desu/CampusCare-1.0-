package com.example.campuscare10.screen

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.campuscare10.datamodel.Equipment
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from

@Composable fun StudentEquipmentDetailScreen(
    navController: NavController,
    equipmentId: String
) {
    val greenColor = Color(0xFF2E8B57)

    var equipment by remember { mutableStateOf<Equipment?>(null) }
    var isLoading by remember { mutableStateOf(true) }

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
        }
    }
}
