package com.ud.hangedgame.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ud.hangedgame.viewmodel.RoomViewModel
import com.ud.hangedgame.views.ui.theme.HangedGameTheme


class RoomActivity : ComponentActivity() {
    private val viewModel: RoomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HangedGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RoomScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun RoomScreen(modifier: Modifier = Modifier, viewModel: RoomViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var roomIdInput by remember { mutableStateOf("") }

    LaunchedEffect(key1 = uiState) {
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }

        if (uiState.matchCreatedId != null) {
            Toast.makeText(context, "Sala creada con ID: ${uiState.matchCreatedId}", Toast.LENGTH_LONG).show()
        }

        if (uiState.matchJoined?.player2Id != null) {
            Toast.makeText(context, "¡Jugador 2 se ha unido! Empezando partida...", Toast.LENGTH_LONG).show()
            viewModel.resetNavigation()
            context.startActivity(Intent(context, GameActivity::class.java).putExtra("MATCH_ID", uiState.matchJoined!!.matchId))
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.createNewMatch(context) },
                enabled = !uiState.isLoading
            ) {
                Text(text = "Nueva Sala")
            }

            Text(
                text = "Ir a una sala existente",
                style = MaterialTheme.typography.headlineSmall
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = roomIdInput,
                    onValueChange = { roomIdInput = it },
                    label = { Text("ID de sala") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.joinExistingMatch(context, roomIdInput) },
                    enabled = !uiState.isLoading
                ) {
                    Text(text = "Unirse")
                }
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (uiState.matchCreatedId != null) {
            Text(
                text = "Id: $uiState.matchCreatedId",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
