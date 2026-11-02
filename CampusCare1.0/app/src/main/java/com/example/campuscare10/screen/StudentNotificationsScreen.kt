package com.example.campuscare10.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.campuscare10.datamodel.Equipment
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun StudentNotificationsScreen(navController: NavController) {
    val primaryColor = Color(0xFF303F9F) // Student Blue Theme
    var equipmentList by remember { mutableStateOf<List<Equipment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch equipment updates from Supabase when the screen loads
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            // Adjust table name if your Supabase table is named differently (e.g. "equipment" or "Equipment")
            val fetchedEquipment = supabase.from("equipment").select().decodeList<Equipment>()
            equipmentList = fetchedEquipment
        } catch (e: Exception) {
            Log.e("StudentNotifications", "Error fetching equipment updates", e)
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("student_dashboard") { popUpTo("student_notifications") { inclusive = true } } },
                    icon = { Text("⌂") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("reports") { popUpTo("student_notifications") { inclusive = true } } },
                    icon = { Text("▤") },
                    label = { Text("Reports") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("☰") },
                    label = { Text("Equipment") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("student_profile") { popUpTo("student_notifications") { inclusive = true } } },
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
                text = "Equipment Updates",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Latest equipment added or updated by staff",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else if (equipmentList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No equipment updates found.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(equipmentList) { equipment ->
                        EquipmentUpdateCard(equipment = equipment, primaryColor = primaryColor, navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentUpdateCard(equipment: Equipment, primaryColor: Color, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                navController.navigate("student_equipment_detail/${equipment.id}")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = equipment.equipmentName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = primaryColor
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFE2E4FF)
                ) {
                    Text(
                        text = equipment.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = primaryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Location: ${equipment.location}",
                fontSize = 13.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = equipment.description,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}