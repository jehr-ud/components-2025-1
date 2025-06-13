package com.ud.hangedgame.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ud.hangedgame.views.ui.theme.HangedGameTheme
import androidx.lifecycle.viewmodel.compose.viewModel // Importar viewModel
import com.ud.hangedgame.repositories.ScoreRepository // Importar ScoreRepository
import com.ud.hangedgame.viewmodel.LevelViewModel // Importar LevelViewModel
import com.ud.hangedgame.viewmodel.LevelViewModelFactory // Importar LevelViewModelFactory
import androidx.compose.runtime.collectAsState // Importar collectAsState
import androidx.compose.runtime.getValue // Importar getValue

class LevelActivity : ComponentActivity() {
    val levels = listOf("A1", "B1", "B2")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HangedGameTheme {
                // Instancia el LevelViewModel
                val scoreRepository = ScoreRepository()
                val levelViewModel: LevelViewModel = viewModel(
                    factory = LevelViewModelFactory(scoreRepository, applicationContext)
                )

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LevelSelection(
                        levels = this.levels,
                        totalScore = levelViewModel.totalScore.collectAsState().value, // Pasa el score
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LevelSelection(levels: List<String>, totalScore: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Muestra el score total del usuario
        Text(
            text = "Tu Puntaje Total: $totalScore",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        //  // Consider adding an image for visual example here

        for (level in levels) {
            LevelItem(level = level)
        }
    }
}

@Composable
fun LevelItem(level: String) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Primer cuadrado: nombre del nivel y barra de progreso
        Box(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Nivel $level", fontSize = 20.sp)
                LinearProgressIndicator(progress = 1f)
            }
        }

        // Segundo cuadrado: botón de inicio
        Box(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                val intent = Intent(context, GameActivity::class.java)
                intent.putExtra("level", level)
                context.startActivity(intent)
            }) {
                Text(text = "Iniciar Juego")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLevelSelection() {
    HangedGameTheme {
        LevelSelection(
            levels = listOf("A1", "B1", "B2"),
            totalScore = 150 // Valor de ejemplo para el score total
        )
    }
}
