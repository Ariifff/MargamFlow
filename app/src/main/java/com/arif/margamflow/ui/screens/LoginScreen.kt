package com.arif.margamflow.ui.screens

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onSignIn: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    onForgetClick: () -> Unit
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val db = Firebase.firestore


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background with gradient
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
                Spacer(modifier = Modifier.height(100.dp))

                // App logo/icon
                Image(
                    painter = painterResource(id = R.drawable.logologin), // Create this drawable
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .height(120.dp)   // control height
                        .wrapContentWidth()
                )

                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Login card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color.Black.copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,

                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        // Username field
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = {
                                Text(
                                    "Username",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            placeholder = { Text("Enter your username") },
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

                        // Password field
                        var passwordVisible by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = {
                                Text(
                                    "Password",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            placeholder = { Text("Enter your password") },
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

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Forgot Password?",
                            color = Color.Black.copy(alpha = 0.8f),
                            modifier = Modifier
                                .align(Alignment.End)
                                .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null // removes grey ripple
                            ) {
                                    isLoading = true
                                    coroutineScope.launch {
                                        delay(1200) // show loader for 1.2s
                                        isLoading = false
                                        onForgetClick()
                                    }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Sign In button
                        Button(
                            onClick = {
                                if (username.isEmpty() || password.isEmpty()) {
                                    Toast.makeText(context,"Please fill all fields", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isLoading = true
                                coroutineScope.launch {
                                    delay(1200) // show loader for 1.2s
                                    db.collection("users").document(username).get()
                                        .addOnSuccessListener { document ->
                                            if (document.exists()) {
                                                val storedHash = document.getString("passwordHash")!!
                                                val storedSalt = document.getString("salt")!!
                                                val isValid = PasswordUtils.verifyPassword(password, storedHash, storedSalt)

                                                if (isValid) {
                                                    Log.d("Login", "Login successful")
                                                    onSignIn(username, password)
                                                } else {
                                                    Toast.makeText(context,"Invalid password", Toast.LENGTH_SHORT).show()
                                                    Log.d("Login", "Invalid password")
                                                }
                                            } else {
                                                Toast.makeText(context,"User not found", Toast.LENGTH_SHORT).show()
                                                Log.d("Login", "User not found")
                                            }
                                            isLoading = false
                                        }
                                        .addOnFailureListener {
                                            Log.w("Login", "Error: ${it.message}")
                                            isLoading = false
                                        }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3D5AFE),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(35.dp) // consistent shape

                        ) {
                            Text(
                                "Sign In",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sign up text
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row {
                                Text(
                                    "Don't have an account? ",
                                    color = Color.Gray
                                )
                                Text(
                                    "Sign Up",
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
                                            onSignUpClick()
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