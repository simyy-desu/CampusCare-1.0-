package com.example.campuscare10.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
import com.example.campuscare10.datamodel.Equipment
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from

@Composable
fun EquipmentListScreen(
    navController: NavController
) {
    val greenColor = Color(0xFF2E8B57)
    val context = LocalContext.current

    var searchText by remember { mutableStateOf("") }
    var equipmentList by remember { mutableStateOf<List<Equipment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch equipment from Supabase
    LaunchedEffect(Unit) {
        try {
            val fetchedEquipment = supabase.from("equipment").select().decodeList<Equipment>()
            equipmentList = fetchedEquipment
            Log.d("Supabase", "Fetched ${fetchedEquipment.size} equipment")
        } catch (e: Exception) {
            Log.e("Supabase", "Error fetching equipment", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            isLoading = false
        }
    }

    val filteredList = equipmentList.filter {
        it.equipmentName.contains(searchText, ignoreCase = true) ||
                it.location.contains(searchText, ignoreCase = true) ||
                it.category.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("staff_dashboard") },
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
                    selected = true,
                    onClick = { },
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
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header with Back Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.navigate("staff_dashboard") { popUpTo("staff_dashboard") { inclusive = true } } }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Equipment Management",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${filteredList.size} equipment found",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 48.dp) // Align with title text
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search equipment...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = greenColor)
                }
            } else if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No equipment found",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = filteredList,
                        key = { it.id ?: "" }
                    ) { equipment ->
                        EquipmentRow(
                        equipment = equipment,
                        onClick = {
                            navController.navigate("equipment_detail/${equipment.id}")
                        }
                    )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add button
            Button(
                onClick = { navController.navigate("add_equipment") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = greenColor
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add New Equipment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EquipmentRow(
    equipment: Equipment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = equipment.equipmentName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = equipment.category,
                fontSize = 14.sp,
                color = Color(0xFF2E8B57)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Location: ${equipment.location}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "ID: ${equipment.id}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
