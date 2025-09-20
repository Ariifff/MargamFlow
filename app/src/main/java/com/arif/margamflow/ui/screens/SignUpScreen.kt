package com.arif.margamflow.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arif.margamflow.PasswordUtils
import com.arif.margamflow.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Username availability states
enum class UsernameStatus {
    AVAILABLE, UNAVAILABLE, CHECKING, NEUTRAL
}

val db = Firebase.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUp: (String, String, String, String?) -> Unit,
    onLoginClick: () -> Unit,
    checkUsernameAvailability: (String) -> Boolean // This would be your actual API call
) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var usernameStatus by remember { mutableStateOf(UsernameStatus.NEUTRAL) }
    var isCheckingUsername by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Debounce username checking
    LaunchedEffect(username) {
        if (username.length > 3) {
            isCheckingUsername = true
            usernameStatus = UsernameStatus.CHECKING
            delay(500) // Debounce for 500ms
            val isAvailable = checkUsernameAvailability(username)
            usernameStatus = if (isAvailable) UsernameStatus.AVAILABLE else UsernameStatus.UNAVAILABLE
            isCheckingUsername = false
        } else if (username.isNotEmpty()) {
            usernameStatus = UsernameStatus.NEUTRAL
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background with gradient (same as login)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE0EAFC), // Light Blue-Gray
                                Color(0xFFCFDEF3)  // Soft Steel Blue
                            ),
                            startY = 0f,
                            endY = 800f
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // App logo/icon (same as login)
                Image(
                    painter = painterResource(id = R.drawable.logologin),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .height(120.dp)   // control height
                        .wrapContentWidth()
                )



                Text(
                    text = "Create your account",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.padding( bottom = 16.dp)
                )

                // Sign up card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color.Black.copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),

                    ) {
                        // Name field (optional)
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = {
                                Text(
                                    "Full Name (optional)",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            placeholder = { Text("Enter your full name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3D5AFE),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                                cursorColor = Color(0xFF3D5AFE)
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Username field with availability check
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = {
                                Text(
                                    "Username *",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            placeholder = { Text("Choose a username") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = when (usernameStatus) {
                                    UsernameStatus.AVAILABLE -> Color(0xFF4CAF50)
                                    UsernameStatus.UNAVAILABLE -> Color(0xFFF44336)
                                    else -> Color(0xFF3D5AFE)
                                },
                                unfocusedBorderColor = when (usernameStatus) {
                                    UsernameStatus.AVAILABLE -> Color(0xFF4CAF50).copy(alpha = 0.6f)
                                    UsernameStatus.UNAVAILABLE -> Color(0xFFF44336).copy(alpha = 0.6f)
                                    else -> Color.Gray.copy(alpha = 0.4f)
                                },
                                cursorColor = Color(0xFF3D5AFE)
                            ),
                            trailingIcon = {
                                if (isCheckingUsername) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFF3D5AFE)
                                    )
                                } else if (username.isNotEmpty() && usernameStatus != UsernameStatus.NEUTRAL) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (usernameStatus == UsernameStatus.AVAILABLE) {
                                                R.drawable.ic_launcher_background
                                            } else {
                                                R.drawable.ic_launcher_foreground
                                            }
                                        ),
                                        contentDescription = "Username status",
                                        tint = if (usernameStatus == UsernameStatus.AVAILABLE) {
                                            Color(0xFF4CAF50)
                                        } else {
                                            Color(0xFFF44336)
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        )

                        // Username status text
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(15.dp),   // Reserve space (adjust as needed)
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (username.isNotEmpty()) {
                                Text(
                                    text = when (usernameStatus) {
                                        UsernameStatus.CHECKING -> "Checking availability..."
                                        UsernameStatus.AVAILABLE -> "✓ Username is available"
                                        UsernameStatus.UNAVAILABLE -> "✗ Username is already taken"
                                        UsernameStatus.NEUTRAL -> "Enter at least 4 characters"
                                    },
                                    color = when (usernameStatus) {
                                        UsernameStatus.CHECKING -> Color(0xFF3D5AFE)
                                        UsernameStatus.AVAILABLE -> Color(0xFF4CAF50)
                                        UsernameStatus.UNAVAILABLE -> Color(0xFFF44336)
                                        UsernameStatus.NEUTRAL -> Color.Gray
                                    },
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 1.dp, start = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        // Email field (optional)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = {
                                Text(
                                    "Email (optional)",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            placeholder = { Text("Enter your email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3D5AFE),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                                cursorColor = Color(0xFF3D5AFE)
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Password field
                        var passwordVisible by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = {
                                Text(
                                    "Password *",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            placeholder = { Text("Create a password") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = Color.Gray
                                    )
                                }
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3D5AFE),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                                cursorColor = Color(0xFF3D5AFE)
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Confirm Password field
                        var confirmPasswordVisible by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = {
                                Text(
                                    "Confirm Password *",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            placeholder = { Text("Confirm your password") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val icon = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                        tint = Color.Gray
                                    )
                                }
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                                    if (password == confirmPassword) Color(0xFF4CAF50) else Color(0xFFF44336)
                                } else {
                                    Color(0xFF3D5AFE)
                                },
                                unfocusedBorderColor = if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                                    if (password == confirmPassword) Color(0xFF4CAF50).copy(alpha = 0.6f) else Color(0xFFF44336).copy(alpha = 0.6f)
                                } else {
                                    Color.Gray.copy(alpha = 0.4f)
                                },
                                cursorColor = Color(0xFF3D5AFE)
                            )
                        )

                        // Password match indicator
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(15.dp),   // Reserve space (adjust as needed)
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                                Text(
                                    text = if (password == confirmPassword) "✓ Passwords match" else "✗ Passwords don't match",
                                    color = if (password == confirmPassword) Color(0xFF4CAF50) else Color(
                                        0xFFF44336
                                    ),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 1.dp, start = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sign Up button
                        val isFormValid = username.isNotEmpty() &&
                                password.isNotEmpty() &&
                                confirmPassword.isNotEmpty() &&
                                password == confirmPassword &&
                                usernameStatus == UsernameStatus.AVAILABLE

                        Button(
                            onClick = {
                                if (isFormValid) {
                                    isLoading = true
                                    coroutineScope.launch {
                                        delay(1200) // show loader for 1.2s
                                        val salt = PasswordUtils.generateSalt()
                                        val hashedPassword = PasswordUtils.hashPassword(password, salt)

                                        val user = hashMapOf(
                                            "username" to username,
                                            "passwordHash" to hashedPassword,
                                            "salt" to salt,
                                            "email" to email.takeIf { it.isNotEmpty() },
                                            "name" to name.takeIf { it.isNotEmpty() }
                                        )

                                        db.collection("users").document(username).set(user)
                                            .addOnSuccessListener {
                                                Log.d("Signup", "User Registered")
                                                isLoading = false
                                                onSignUp(username, password, name, email.takeIf { it.isNotEmpty() })
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w("Signup", "Error", e)
                                                isLoading = false
                                            }
                                    }

                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFormValid) Color(0xFF3D5AFE) else Color.Gray.copy(alpha = 0.5f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(35.dp),
                            enabled = isFormValid
                        ) {
                            Text(
                                "Sign Up",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Login text
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row {
                                Text(
                                    "Already have an account? ",
                                    color = Color.Gray
                                )
                                Text(
                                    "Login",
                                    color = Color(0xFF3D5AFE),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable (
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ){
                                        isLoading = true
                                        coroutineScope.launch {
                                            delay(1200) // show loader for 1.2s
                                            isLoading = false
                                            onLoginClick()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF3D5AFE),
                        strokeWidth = 3.dp
                    )
                }
            }
        }
    }
}
