package com.ud.hangedgame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ud.hangedgame.ui.theme.HangedGameTheme



class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent{
            Column(modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text("Bienvenido",
                    fontSize = 32.sp
                    )

                val correo = remember { mutableStateOf("") }
                val password = remember { mutableStateOf("") }
                TextField(
                    value = correo.value,
                    onValueChange = { correo.value = it },
                    label = { Text("Correo") }
                )

                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Contraseña") }
                )

                Button(onClick = {
                    startActivity(Intent(this@LoginActivity, LevelActivity::class.java))
                }) {
                    Text("Login")
                }

            }
        }
    }
}