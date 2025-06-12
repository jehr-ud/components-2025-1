package com.ud.hangedgame.views

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ud.hangedgame.views.ui.theme.HangedGameTheme

class RoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HangedGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RoomScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun RoomScreen(modifier: Modifier = Modifier) {
    // Accedemos al contexto con LocalContext
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botón para crear una nueva sala
        Button(onClick = {
            // Usamos el contexto para mostrar el Toast
            Toast.makeText(context, "Creando nueva sala...", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "Nueva Sala")
        }

        // Texto y caja para ir a una sala existente
        Text(
            text = "Ir a una sala existente",
            style = MaterialTheme.typography.headlineSmall // Actualizado a Material 3
        )
        Row {
            TextField(
                value = "",
                onValueChange = {},
                label = { Text("ID de sala") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                // Lógica para unirse a la sala con el ID proporcionado
                Toast.makeText(context, "Uniéndote a la sala...", Toast.LENGTH_SHORT).show()
            }) {
                Text(text = "Unirse")
            }
        }
    }
}
