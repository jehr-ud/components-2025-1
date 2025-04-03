package com.ud.hangedgame

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.ud.hangedgame.ui.theme.HangedGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HangedGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Game(
                        words = "________",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Game(words: String, modifier: Modifier = Modifier) {
    var texto by remember { mutableStateOf("") }
    val contexto = LocalContext.current
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_background),
        contentDescription = "Descripción de la imagen"
    )
    Text(
        text = "$words!",
        modifier = modifier
    )
    TextField(
        value = texto,
        onValueChange = { texto = it },
        label = { Text("Give me a character") }
    )
    Button(onClick = {
        Toast.makeText(contexto, "¡Botón pulsado!", Toast.LENGTH_SHORT).show()
    }) {
        Text("Haz clic aquí")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HangedGameTheme {
        Game("Android")
    }
}