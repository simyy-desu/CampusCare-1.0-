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
import com.example.campuscare10.datamodel.StudentProfiles
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun StudentLoginScreen(
    onLoginSuccess: (StudentProfiles) -> Unit,
    onNavigateToRegister: () -> Unit,
    onBackClick: () -> Unit
) {
    val primaryPurple = Color(0xFF6C47FF)
    val backgroundTint = Color(0xFFF8F8FC)

    var studentIdOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isStudentIdError by remember { mutableStateOf<String?>(null) }
    var isPasswordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundTint
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Student Login",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = primaryPurple,
                        fontSize = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Sign in to access your reports",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = studentIdOrEmail,
                    onValueChange = {
                        studentIdOrEmail = it
                        isStudentIdError = null
                    },
                    label = {
                        Text(
                            text = isStudentIdError ?: "Student ID / Email",
                            color = if (isStudentIdError != null) MaterialTheme.colorScheme.error else Color.Unspecified
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isStudentIdError != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryPurple,
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
                        isPasswordError = null
                    },
                    label = {
                        Text(
                            text = isPasswordError ?: "Password",
                            color = if (isPasswordError != null) MaterialTheme.colorScheme.error else Color.Unspecified
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = isPasswordError != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryPurple,
                        errorBorderColor = Color.Red,
                        errorLabelColor = Color.Red
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = passwordVisible,
                        onCheckedChange = { passwordVisible = it },
                        colors = CheckboxDefaults.colors(checkedColor = primaryPurple)
                    )
                    Text(text = "Show Password", style = MaterialTheme.typography.bodyMedium)
                }

                Button(
                    onClick = {
                        isStudentIdError = null
                        isPasswordError = null

                        if (studentIdOrEmail.isBlank()) {
                            isStudentIdError = "Student ID or Email required"
                        }
                        if (password.isBlank()) {
                            isPasswordError = "Password required"
                        }

                        if (isStudentIdError != null || isPasswordError != null) return@Button

                        coroutineScope.launch {
                            isLoading = true
                            try {
                                // Query Supabase checking if input matches either Studentid OR Email
                                val studentRecord = supabase.from("Studentprofiles")
                                    .select {
                                        filter {
                                            or {
                                                eq("Studentid", studentIdOrEmail)
                                                eq("Email", studentIdOrEmail)
                                            }
                                        }
                                    }.decodeSingleOrNull<StudentProfiles>()

                                if (studentRecord == null) {
                                    isStudentIdError = "Wrong Student ID or Email"
                                } else if (studentRecord.password != password) {
                                    isPasswordError = "Wrong Password"
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Welcome, ${studentRecord.studentName ?: studentRecord.studentId}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onLoginSuccess(studentRecord)
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
                    colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Don't have an account?", color = Color.Gray, fontSize = 14.sp)
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            text = "Register",
                            color = primaryPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onBackClick) {
                    Text("Back to Splash", color = Color.Gray)
                }
            }
        }
    }
}