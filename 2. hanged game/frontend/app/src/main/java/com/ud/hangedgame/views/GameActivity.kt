package com.ud.hangedgame.views

import androidx.lifecycle.viewmodel.compose.viewModel
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ud.hangedgame.R
import com.ud.hangedgame.repositories.WordRepository
import com.ud.hangedgame.viewmodel.GameViewModel
import com.ud.hangedgame.viewmodel.GameViewModelFactory
import com.ud.hangedgame.views.ui.theme.HangedGameTheme


class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = getIntent()

        val level: String = intent.getStringExtra("level") ?: "B1"

        Toast.makeText(
            this,
            "Start game for level $level",
            Toast.LENGTH_LONG
        ).show()

        setContent {

            val gameViewModel: GameViewModel = viewModel(
                factory = GameViewModelFactory(WordRepository())
            )

            LaunchedEffect(level) {
                gameViewModel.loadNewWord(level)
            }

            HangmanGameScreen(gameViewModel = gameViewModel)
        }
    }
}

@Composable
fun HangmanGameScreen(
    gameViewModel: GameViewModel = viewModel()
) {
    val secretWord by gameViewModel.secretWord.collectAsState()
    val guessedLetters by gameViewModel.guessedLetters.collectAsState()
    val errors by gameViewModel.errors.collectAsState()
    val hasWon by gameViewModel.hasWon.collectAsState()
    val hasLost by gameViewModel.hasLost.collectAsState()
    val isLoading by gameViewModel.isLoading.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (secretWord != null) {
                HangmanGame(
                    secretWord = secretWord!!,
                    guessedLetters = guessedLetters,
                    errors = errors,
                    hasWon = hasWon,
                    hasLost = hasLost,
                    onLetterSelected = { letter -> gameViewModel.guessLetter(letter) }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se pudo cargar la palabra. Inténtalo de nuevo.")
                }
            }
        }
    }
}

@Composable
fun HangmanGame(
    secretWord: String,
    guessedLetters: Set<Char>,
    errors: Int,
    hasWon: Boolean,
    hasLost: Boolean,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier
)  {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = getImageForError(errors)),
            contentDescription = "Ahorcado - error $errors",
            modifier = Modifier
                .padding(top = 24.dp)
                .size(250.dp)
        )

        Row {
            secretWord.forEach { letter ->
                Text(
                    text = if (guessedLetters.contains(letter)) "$letter " else "_ ",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }


        Keyboard(
            onLetterSelected = onLetterSelected,
            disabledLetters = guessedLetters,
            enabled = !hasWon && !hasLost
        )

        when {
            hasWon -> Text("¡You won!")
            hasLost -> Text("You lost. The word was: $secretWord")
        }
    }
}

@Composable
fun getImageForError(errors: Int): Int {
    return when (errors) {
        0 -> R.drawable.hanged_0
        1 -> R.drawable.hanged_1
        2 -> R.drawable.hanged_2
        3 -> R.drawable.hanged_3
        4 -> R.drawable.hanged_4
        else -> R.drawable.hanged_5
    }
}

@Composable
fun Keyboard(
    onLetterSelected: (Char) -> Unit,
    disabledLetters: Set<Char>,
    enabled: Boolean
) {
    val letters = ('A'..'Z').toList()
    val rows = letters.chunked(5)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        rows.forEach { rowLetters ->
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 3.dp)
            ) {
                rowLetters.forEach { letter ->
                    Button(
                        onClick = { onLetterSelected(letter) },
                        enabled = enabled && !disabledLetters.contains(letter.lowercaseChar()), // Aplicar 'enabled'
                        modifier = Modifier.padding(3.dp)
                    ) {
                        Text(
                            text = letter.toString(),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHangmanGame() {
    HangedGameTheme {
        HangmanGame(
            secretWord = "components",
            guessedLetters = setOf('c', 'o', 'm', 'p', 's'),
            errors = 2,
            hasWon = false,
            hasLost = false,
            onLetterSelected = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
