package com.ud.hangedgame.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private val PREFS_NAME = "VocaUDPrefs"
    private val KEY_USER_ID = "userId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth

        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this@LoginActivity, RoomActivity::class.java))
            finish()
            return
        }

        setContent {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Bienvenido", fontSize = 32.sp)

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this@LoginActivity) { task ->
                                    if (task.isSuccessful) {
                                        Log.d("vocaud-login", "createUserWithEmail:success")
                                        val user = auth.currentUser
                                        val uid = user?.uid
                                        if (uid != null) {
                                            saveUserInSharedPreferences(uid)
                                        }
                                        startActivity(Intent(this@LoginActivity, LevelActivity::class.java))
                                        finish()
                                    } else {
                                        Log.w("vocaud-login", "createUserWithEmail:failure", task.exception)
                                        Toast.makeText(
                                            baseContext,
                                            "Error de autenticación: ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this@LoginActivity, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Login")
                }
            }
        }
    }

    private fun saveUserInSharedPreferences(uid: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, uid)
        editor.apply()
    }
}
