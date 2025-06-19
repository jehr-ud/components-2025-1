package com.ud.hangedgame.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ud.hangedgame.views.ui.theme.HangedGameTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ud.hangedgame.models.Level
import com.ud.hangedgame.viewmodel.LevelViewModel
import com.ud.hangedgame.viewmodel.LevelViewModelFactory
import com.ud.hangedgame.repositories.LevelRepository
import com.ud.hangedgame.providers.network.AppModule
import com.ud.hangedgame.repositories.ScoreRepository

class LevelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HangedGameTheme {
                // Initialize the repository and factory
                val levelRepository = LevelRepository(AppModule.levelApiService)
                val scoreRepository = ScoreRepository()
                val factory = LevelViewModelFactory(scoreRepository, levelRepository, this)

                // Get the ViewModel
                val levelViewModel: LevelViewModel = viewModel(factory = factory)

                // Observe LiveData from the ViewModel
                val levels = levelViewModel.levels.observeAsState(initial = emptyList()).value
                val totalScore = levelViewModel.totalScore.collectAsStateWithLifecycle().value
                val errorMessage = levelViewModel.errorMessage.observeAsState().value
                val isLoading = levelViewModel.isLoading.observeAsState(initial = false).value

                // Display error messages
                errorMessage?.let { message ->
                    Toast.makeText(LocalContext.current, message, Toast.LENGTH_LONG).show()
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LevelSelection(
                        levels = levels, // Pass the fetched levels
                        totalScore = totalScore, // Pass the total score
                        isLoading = isLoading, // Pass loading state
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LevelSelection(levels: List<Level>, totalScore: Int, isLoading: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Tu Puntaje Total: $totalScore",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
            Text(text = "Cargando niveles...", style = MaterialTheme.typography.bodyLarge)
        } else if (levels.isEmpty()) {
            Text(text = "No hay niveles disponibles.", style = MaterialTheme.typography.bodyLarge)
        } else {
            for (level in levels) {
                LevelItem(level = level)
            }
        }
    }
}

@Composable
fun LevelItem(level: Level) { // Use the actual Level data class
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                Text(text = "Nivel ${level.name}", fontSize = 20.sp) // Use level.name
                // You'll need actual progress values from your API or game state for this
                LinearProgressIndicator(progress = 0.5f) // Placeholder progress
            }
        }

        Box(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                if (level.isUsable){
                    val intent = Intent(context, RoomActivity::class.java)
                    intent.putExtra("levelId", level.id)
                    intent.putExtra("levelName", level.name)
                    context.startActivity(intent)
                }
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
            levels = listOf(
                Level(id = "1", name = "A1", isUsable = true),
                Level(id = "2", name = "B1", isUsable = true)
            ),
            totalScore = 190,
            isLoading = false
        )
    }
}