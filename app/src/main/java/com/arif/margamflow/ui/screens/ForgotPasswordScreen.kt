package com.arif.margamflow.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.arif.margamflow.PasswordUtils
import com.arif.margamflow.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController : NavController,
    onBackClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var answer1 by remember { mutableStateOf("") }
    var answer2 by remember { mutableStateOf("") }
    var answer3 by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var showRecoveryQuestions by remember { mutableStateOf(false) }
    var showPasswordReset by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    // Store fetched user data
    var storedSalt by remember { mutableStateOf("") }
    var storedHashedAnswers by remember { mutableStateOf(listOf<String>()) }

    val db = FirebaseFirestore.getInstance()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFE0EAFC), Color(0xFFCFDEF3))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                "Recover Password",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color(0xFF3D5AFE)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Enter your username to start recovery",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                Column(modifier = Modifier.padding(24.dp)) {

                    // Username Input
                    if (!showRecoveryQuestions && !showPasswordReset) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3D5AFE),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                                cursorColor = Color(0xFF3D5AFE)
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (username.isBlank()) {
                                    errorMessage = "Please enter a username"
                                    return@Button
                                }
                                // Fetch user from Firestore
                                db.collection("users").document(username).get()
                                    .addOnSuccessListener { doc ->
                                        if (doc.exists()) {
                                            storedSalt = doc.getString("salt") ?: ""
                                            storedHashedAnswers = listOf(
                                                doc.getString("hashedanswer1") ?: "",
                                                doc.getString("hashedanswer2") ?: "",
                                                doc.getString("hashedanswer3") ?: ""
                                            )
                                            showRecoveryQuestions = true
                                            errorMessage = ""
                                        } else {
                                            errorMessage = "User not found"
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        errorMessage = "Error fetching user"
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5AFE)),
                            shape = RoundedCornerShape(35.dp)
                        ) {
                            Text("Next", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        if (errorMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                        }
                    }

                    // Recovery Questions Section
                    AnimatedVisibility(visible = showRecoveryQuestions && !showPasswordReset) {
                        Column {
                            OutlinedTextField(
                                value = answer1,
                                onValueChange = { answer1 = it },
                                label = { Text("What is your first pet’s name?") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = answer2,
                                onValueChange = { answer2 = it },
                                label = { Text("What is your favorite teacher’s name?") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = answer3,
                                onValueChange = { answer3 = it },
                                label = { Text("Which city were you born in?") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (errorMessage.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (storedSalt.isBlank() || storedHashedAnswers.isEmpty()) return@Button
                                    if(answer1.isBlank() || answer2.isBlank() || answer3.isBlank()) return@Button
                                    val userAnswers = listOf(answer1, answer2, answer3)
                                    val correctCount = userAnswers.zip(storedHashedAnswers)
                                        .count { (input, storedHash) ->
                                            PasswordUtils.verifyPassword(input, storedHash, storedSalt)
                                        }
                                    if (correctCount >= 2) {
                                        showPasswordReset = true
                                        errorMessage = ""
                                    } else {
                                        errorMessage = "At least 2 answers must be correct"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5AFE)),
                                shape = RoundedCornerShape(35.dp)
                            ) {
                                Text("Verify", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // New Password Section
                    AnimatedVisibility(visible = showPasswordReset) {
                        Column {
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("Enter new password") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    isLoading= true
                                    if (newPassword.isNotBlank()) {
                                        // Hash new password using stored salt
                                        val hashedPassword = PasswordUtils.hashPassword(newPassword, storedSalt)
                                        // Update in Firestore
                                        db.collection("users").document(username)
                                            .update("passwordHash", hashedPassword)
                                            .addOnSuccessListener {
                                                errorMessage = "Password reset successful!"
                                                isLoading=false
                                                navController.navigate(Screen.Login.route) {
                                                    popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                                                }
                                            }
                                            .addOnFailureListener { errorMessage = "Error updating password" }
                                    } else {
                                        isLoading=false
                                        errorMessage = "Password cannot be empty"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5AFE)),
                                shape = RoundedCornerShape(35.dp)
                            ) {
                                Text("Reset Password", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            if (errorMessage.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onBackClick) {
                        Text("Back", color = Color(0xFF3D5AFE))
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
