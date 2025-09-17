package com.arif.margamflow.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arif.margamflow.PasswordUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRecoveryScreen(
    username: String,
    onSubmit: (String, String, String) -> Unit
) {
    var answer1 by remember { mutableStateOf("") }
    var answer2 by remember { mutableStateOf("") }
    var answer3 by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = Firebase.firestore
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Disable device back button
    BackHandler(enabled = true) {
        // Do nothing -> back press ignored
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFFE0EAFC),
                        Color(0xFFCFDEF3)
                    )
                )
            ),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Password Recovery",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3D5AFE)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Answer the questions to reset your password in future.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Q1
            OutlinedTextField(
                value = answer1,
                onValueChange = { answer1 = it },
                label = { Text("What is your mother's mother name?") },
                placeholder = { Text("Enter answer") },
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

            // Q2
            OutlinedTextField(
                value = answer2,
                onValueChange = { answer2 = it },
                label = { Text("What was the name of your first pet?") },
                placeholder = { Text("Enter answer") },
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

            // Q3
            OutlinedTextField(
                value = answer3,
                onValueChange = { answer3 = it },
                label = { Text("What city were you born in?") },
                placeholder = { Text("Enter answer") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF3D5AFE),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                    cursorColor = Color(0xFF3D5AFE)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit button
            Button(
                onClick = {
                    if(answer1.isNotEmpty() && answer2.isNotEmpty() && answer3.isNotEmpty()){
                        isLoading = true
                        coroutineScope.launch {
                            delay(1200) // show loader for 1.2s

                            // 1️⃣ Retrieve the salt of the user from Firestore first
                            db.collection("users").document(username).get()
                                .addOnSuccessListener { document ->
                                    val salt = document.getString("salt")
                                    if (salt != null) {
                                        // 2️⃣ Hash recovery answers using the same salt
                                        val hashedAnswer1 = PasswordUtils.hashPassword(answer1, salt)
                                        val hashedAnswer2 = PasswordUtils.hashPassword(answer2, salt)
                                        val hashedAnswer3 = PasswordUtils.hashPassword(answer3, salt)

                                        // 3️⃣ Prepare map to update answers without overwriting other data
                                        val updates = hashMapOf(
                                            "hashedanswer1" to hashedAnswer1,
                                            "hashedanswer2" to hashedAnswer2,
                                            "hashedanswer3" to hashedAnswer3
                                        )

                                        // 4️⃣ Merge updates with existing document
                                        db.collection("users").document(username)
                                            .set(updates, SetOptions.merge())
                                            .addOnSuccessListener {
                                                Log.d("Signup", "Recovery answers added successfully")
                                                isLoading = false
                                                onSubmit(answer1, answer2, answer3)
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w("Signup", "Error adding recovery answers", e)
                                                isLoading = false
                                            }
                                    } else {
                                        Log.e("Signup", "Salt not found for user")
                                        isLoading = false
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Signup", "Error fetching user data", e)
                                    isLoading = false
                                }
                        }


                    }
                    else{
                        Toast.makeText(context,"Please answer all questions", Toast.LENGTH_SHORT).show()
                    }
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5AFE))
            ) {
                Text("Submit", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

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

