package com.ud.hangedgame

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ud.hangedgame.ui.theme.HangedGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HangedGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HangmanGame(secretWord = "components", modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun HangmanGame(secretWord: String, modifier: Modifier = Modifier) {
    var guessedLetters by remember { mutableStateOf(setOf<Char>()) }
    var errors by remember { mutableIntStateOf(0) }

    val hasWon = secretWord.all { guessedLetters.contains(it) }
    val hasLost = errors >= 5

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
            onLetterSelected = { letter ->
                val lowerLetter = letter.lowercaseChar()
                if (!guessedLetters.contains(lowerLetter)) {
                    if (secretWord.contains(lowerLetter, ignoreCase = true)) {
                        guessedLetters = guessedLetters + lowerLetter
                    } else {
                        errors++
                    }
                }
            },
            disabledLetters = guessedLetters
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
    disabledLetters: Set<Char>
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
                        enabled = !disabledLetters.contains(letter.lowercaseChar()),
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
        HangmanGame(secretWord = "components")
    }
}