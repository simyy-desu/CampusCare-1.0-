package com.example.campuscare10.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuscare10.datamodel.StaffProfile
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun StaffLoginScreen(
    onLoginSuccess: (StaffProfile) -> Unit,
    onBack: () -> Unit
) {
    var staffId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var staffIdError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val staffGreen = Color(0xFF388E3C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Staff Login",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = staffGreen
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = staffId,
            onValueChange = { 
                staffId = it
                staffIdError = null 
            },
            label = { 
                Text(
                    text = staffIdError ?: "Staff ID",
                    color = if (staffIdError != null) MaterialTheme.colorScheme.error else Color.Unspecified
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            isError = staffIdError != null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = staffGreen,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = null
            },
            label = { 
                Text(
                    text = passwordError ?: "Password",
                    color = if (passwordError != null) MaterialTheme.colorScheme.error else Color.Unspecified
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = passwordError != null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = staffGreen,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red
            ),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = passwordVisible,
                onCheckedChange = { passwordVisible = it },
                colors = CheckboxDefaults.colors(checkedColor = staffGreen)
            )
            Text(text = "Show Password", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                staffIdError = null
                passwordError = null

                if (staffId.isBlank()) {
                    staffIdError = "Staff ID required"
                }
                if (password.isBlank()) {
                    passwordError = "Password required"
                }

                if (staffIdError != null || passwordError != null) return@Button

                coroutineScope.launch {
                    isLoading = true
                    try {
                        val staffRecord = supabase.from("Staffprofile")
                            .select {
                                filter {
                                    eq("Staffid", staffId)
                                }
                            }.decodeSingleOrNull<StaffProfile>()

                        if (staffRecord == null) {
                            staffIdError = "Wrong Staff ID"
                        } else if (staffRecord.password != password) {
                            passwordError = "Wrong Password"
                        } else {
                            Toast.makeText(context, "Welcome, ${staffRecord.staffName ?: staffRecord.staffId}", Toast.LENGTH_SHORT).show()
                            onLoginSuccess(staffRecord)
                        }
                    } catch (e: Exception) {
                        Log.e("Login", "Error", e)
                        Toast.makeText(context, "Login Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = staffGreen),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("Back to Splash", color = Color.Gray)
        }
    }
}
