package com.example.campuscare10.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuscare10.datamodel.StudentProfiles
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegistrationSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val primaryPurple = Color(0xFF6C47FF)
    val backgroundTint = Color(0xFFF8F8FC)
    val coroutineScope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val departmentOptions = listOf("FOCS", "FISH", "FOBE", "FAFB", "FCCI", "FAHS")

        var usernameErrorMsg by remember { mutableStateOf<String?>(null) }
    var departmentErrorMsg by remember { mutableStateOf<String?>(null) }
    var emailErrorMsg by remember { mutableStateOf<String?>(null) }
    var contactErrorMsg by remember { mutableStateOf<String?>(null) }
    var passwordErrorMsg by remember { mutableStateOf<String?>(null) }
    var confirmPasswordErrorMsg by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundTint
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }


            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Register account",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = primaryPurple,
                        fontSize = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                @Composable
                fun RegisterTextField(
                    label: String,
                    value: String,
                    onValueChange: (String) -> Unit,
                    icon: androidx.compose.ui.graphics.vector.ImageVector,
                    isError: Boolean = false,
                    fieldErrorMsg: String? = null,
                    helperText: String? = null,
                    keyboardType: KeyboardType = KeyboardType.Text,
                    isPassword: Boolean = false,
                    passwordVisibleState: Boolean = false,
                    onPasswordVisibilityToggle: () -> Unit = {}
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isError) Color.Red else Color.DarkGray,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = value,
                            onValueChange = onValueChange,
                            placeholder = { Text("Please enter your ${label.lowercase()}...", color = Color.Gray, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(imageVector = icon, contentDescription = null, tint = if (isError) Color.Red else Color.Gray, modifier = Modifier.size(20.dp))
                            },
                            trailingIcon = {
                                if (isPassword) {
                                    IconButton(onClick = onPasswordVisibilityToggle) {
                                        Icon(
                                            imageVector = if (passwordVisibleState) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password visibility",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            },
                            isError = isError,
                            visualTransformation = if (isPassword && !passwordVisibleState) PasswordVisualTransformation() else VisualTransformation.None,
                            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryPurple,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                errorBorderColor = Color.Red,
                                errorLeadingIconColor = Color.Red
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        )


                        Spacer(modifier = Modifier.height(4.dp))
                        if (isError && !fieldErrorMsg.isNullOrBlank()) {
                            Text(
                                text = fieldErrorMsg,
                                color = Color.Red,
                                fontSize = 11.sp
                            )
                        } else if (!helperText.isNullOrBlank()) {
                            Text(
                                text = helperText,
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                RegisterTextField(
                    label = "Name",
                    value = username,
                    onValueChange = { username = it; usernameErrorMsg = null },
                    icon = Icons.Default.Person,
                    isError = usernameErrorMsg != null,
                    fieldErrorMsg = usernameErrorMsg,
                    helperText = "Enter your full legal name."
                )


                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Department",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (departmentErrorMsg != null) Color.Red else Color.DarkGray,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = department,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Select your department...", color = Color.Gray, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = if (departmentErrorMsg != null) Color.Red else Color.Gray, modifier = Modifier.size(20.dp))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            isError = departmentErrorMsg != null,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryPurple,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                errorBorderColor = Color.Red
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .height(52.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            departmentOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        department = option
                                        departmentErrorMsg = null
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    if (departmentErrorMsg != null) {
                        Text(text = departmentErrorMsg!!, color = Color.Red, fontSize = 11.sp)
                    } else {
                        Text(text = "Choose your respective faculty/department.", color = Color.Gray, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                RegisterTextField(
                    label = "Email Address",
                    value = email,
                    onValueChange = { email = it; emailErrorMsg = null },
                    icon = Icons.Default.Email,
                    isError = emailErrorMsg != null,
                    fieldErrorMsg = emailErrorMsg,
                    helperText = "Must match format: username@student.xxxx.edu.my",
                    keyboardType = KeyboardType.Email
                )

                RegisterTextField(
                    label = "Contact Number",
                    value = contactNumber,
                    onValueChange = { if (it.length <= 11) { contactNumber = it; contactErrorMsg = null } },
                    icon = Icons.Default.Phone,
                    isError = contactErrorMsg != null,
                    fieldErrorMsg = contactErrorMsg,
                    helperText = "Enter up to 11 digits (e.g., 01112345678).",
                    keyboardType = KeyboardType.Phone
                )

                RegisterTextField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it; passwordErrorMsg = null },
                    icon = Icons.Default.Lock,
                    isError = passwordErrorMsg != null,
                    fieldErrorMsg = passwordErrorMsg,
                    helperText = "Must be at least 6 characters long.",
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    passwordVisibleState = passwordVisible,
                    onPasswordVisibilityToggle = { passwordVisible = !passwordVisible }
                )

                RegisterTextField(
                    label = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; confirmPasswordErrorMsg = null },
                    icon = Icons.Default.Lock,
                    isError = confirmPasswordErrorMsg != null,
                    fieldErrorMsg = confirmPasswordErrorMsg,
                    helperText = "Re-enter your password to confirm.",
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    passwordVisibleState = confirmPasswordVisible,
                    onPasswordVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    var hasError = false

                    if (username.isBlank()) {
                        usernameErrorMsg = "Name cannot be empty!"
                        hasError = true
                    }
                    if (department.isBlank()) {
                        departmentErrorMsg = "Please select a department!"
                        hasError = true
                    }
                    if (email.isBlank()) {
                        emailErrorMsg = "Email cannot be empty!"
                        hasError = true
                    }
                    if (contactNumber.isBlank() || contactNumber.length > 11) {
                        contactErrorMsg = "Enter a valid contact number (max 11 digits)."
                        hasError = true
                    }
                    if (password.isBlank() || password.length < 6) {
                        passwordErrorMsg = "Password must be at least 6 characters."
                        hasError = true
                    }
                    if (confirmPassword.isBlank()) {
                        confirmPasswordErrorMsg = "Please confirm your password."
                        hasError = true
                    }

                    if (hasError) {
                        errorMessage = "Please fix the highlighted fields above."
                        return@Button
                    }

                    // Email format validation matching @student.xxxx.edu.my pattern
                    val studentEmailRegex = "^[A-Za-z0-9._%+-]+@student\\.[A-Za-z0-9-]+\\.edu\\.my$".toRegex()
                    if (!email.matches(studentEmailRegex)) {
                        emailErrorMsg = "Invalid format: username@student.xxxx.edu.my"
                        errorMessage = "Please correct your student email format."
                        return@Button
                    }

                    // Check password match
                    if (password != confirmPassword) {
                        passwordErrorMsg = "Passwords do not match."
                        confirmPasswordErrorMsg = "Passwords do not match."
                        errorMessage = "Passwords do not match!"
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    coroutineScope.launch {
                        try {
                            supabase.auth.signUpWith(Email) {
                                this.email = email
                                this.password = password
                            }

                            val newProfile = StudentProfiles(
                                studentName = username,
                                email = email,
                                department = department,
                                password = password,
                                rating = 5.0,
                                phoneNumber = contactNumber
                            )

                            supabase.from("Studentprofiles").insert(newProfile)

                            isLoading = false
                            onRegistrationSuccess()
                        } catch (e: Exception) {
                            isLoading = false
                            Log.e("Registration", "Error: ${e.message}", e)
                            errorMessage = when {
                                e.message?.contains("unique constraint") == true || e.message?.contains("already registered", ignoreCase = true) == true -> {
                                    emailErrorMsg = "Email already registered!"
                                    "Email already registered!"
                                }
                                else -> e.localizedMessage ?: "Registration failed. Please try again."
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Register",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}