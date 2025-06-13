// src/main/java/com/ud/hangedgame/viewmodel/GameViewModel.kt
package com.ud.hangedgame.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.hangedgame.models.Score
import com.ud.hangedgame.repositories.ScoreRepository
import com.ud.hangedgame.repositories.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val wordRepository: WordRepository = WordRepository(),
    private val scoreRepository: ScoreRepository = ScoreRepository(),
    private val context: Context
) : ViewModel() {

    private val PREFS_NAME = "VocaUDPrefs"
    private val KEY_USER_ID = "userId"

    // --- Constantes de Puntuación (ACTUALIZADAS) ---
    companion object {
        // Puntos que vale cada letra de la palabra si se gana
        private const val POINTS_PER_LETTER_WIN = 5

        // Penalización por cada letra que NO se adivina al perder
        private const val PENALTY_PER_UNREVEALED_LETTER_ON_LOSS = 5

        // Multiplicadores por nivel (Confirmados por el usuario)
        private val LEVEL_MULTIPLIERS = mapOf(
            "A1" to 1.0,
            "B1" to 1.5,
            "B2" to 2.0
        )
    }

    private fun getLevelMultiplier(level: String): Double {
        return LEVEL_MULTIPLIERS[level] ?: 1.0 // Por defecto 1.0 si el nivel no se encuentra
    }
    // --- Fin de Constantes de Puntuación ---

    private val _secretWord = MutableStateFlow<String?>(null)
    val secretWord: StateFlow<String?> = _secretWord.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _guessedLetters = MutableStateFlow<Set<Char>>(emptySet())
    val guessedLetters: StateFlow<Set<Char>> = _guessedLetters.asStateFlow()

    private val _errors = MutableStateFlow(0)
    val errors: StateFlow<Int> = _errors.asStateFlow()

    private val _hasWon = MutableStateFlow(false)
    val hasWon: StateFlow<Boolean> = _hasWon.asStateFlow()

    private val _hasLost = MutableStateFlow(false)
    val hasLost: StateFlow<Boolean> = _hasLost.asStateFlow()

    private val _gameScoreValue = MutableStateFlow(0)
    val gameScoreValue: StateFlow<Int> = _gameScoreValue.asStateFlow()

    private val MAX_ERRORS = 5 // Se mantiene igual el número máximo de errores
    private var currentLevel: String = "B1"

    init {
        viewModelScope.launch {
            hasWon.collect { won ->
                if (won) {
                    saveGameScore(isWin = true)
                }
            }
        }
        viewModelScope.launch {
            hasLost.collect { lost ->
                if (lost) {
                    saveGameScore(isWin = false)
                }
            }
        }
    }

    fun loadNewWord(level: String) {
        _isLoading.value = true
        currentLevel = level
        wordRepository.getRandomWordByLevel(level) { word, exception ->
            if (word != null) {
                _secretWord.value = word.word?.lowercase()
                _guessedLetters.value = emptySet()
                _errors.value = 0
                _hasWon.value = false
                _hasLost.value = false
                _gameScoreValue.value = 0 // Reiniciar el score al iniciar nueva partida
            } else {
                println("Error cargando palabra: ${exception?.message}")
                _secretWord.value = null
            }
            _isLoading.value = false
        }
    }

    fun guessLetter(letter: Char) {
        val currentSecretWord = _secretWord.value
        val lowerLetter = letter.lowercaseChar()

        if (currentSecretWord == null || _hasWon.value || _hasLost.value) {
            return
        }

        if (_guessedLetters.value.contains(lowerLetter)) {
            return
        }

        val newGuessedLetters = _guessedLetters.value + lowerLetter
        _guessedLetters.value = newGuessedLetters

        if (!currentSecretWord.contains(lowerLetter)) {
            val newErrors = _errors.value + 1
            _errors.value = newErrors
            if (newErrors >= MAX_ERRORS) {
                _hasLost.value = true
            }
        }

        if (currentSecretWord.all { newGuessedLetters.contains(it) }) {
            _hasWon.value = true
        }
    }

    private fun saveGameScore(isWin: Boolean) {
        val userId = getUserIdFromSharedPreferences()
        val secretWordValue = _secretWord.value // Obtener la palabra secreta actual

        if (userId == null || secretWordValue == null) {
            println("No se pudo obtener userId o secretWord. No se guardará el score.")
            return
        }

        val calculatedScore: Int = if (isWin) {
            // Lógica de puntuación para VICTORIA: Longitud de palabra * Puntos_Por_Letra
            val basePoints = secretWordValue.length * POINTS_PER_LETTER_WIN
            ((basePoints) * getLevelMultiplier(currentLevel)).toInt()
        } else {
            // Lógica de puntuación para DERROTA: -(Letras_No_Adivinadas * Penalización_Por_Letra_No_Adivinada)
            val revealedLettersCount = secretWordValue.count { guessedLetters.value.contains(it) }
            val unguessedLettersCount = secretWordValue.length - revealedLettersCount
            val unrevealedLetterPenalty = unguessedLettersCount * PENALTY_PER_UNREVEALED_LETTER_ON_LOSS

            // El score será negativo si se pierde
            -((unrevealedLetterPenalty) * getLevelMultiplier(currentLevel)).toInt()
        }

        _gameScoreValue.value = calculatedScore // Actualiza el StateFlow para el UI

        val score = Score(
            scoreValue = calculatedScore,
            level = currentLevel,
            timestamp = System.currentTimeMillis()
        )

        scoreRepository.saveScore(
            userId = userId,
            score = score,
            onSuccess = {
                println("Score guardado en Firebase con éxito para el usuario $userId: $calculatedScore")
            },
            onFailure = { exception ->
                println("Error al guardar score en Firebase para el usuario $userId: ${exception.message}")
            }
        )
    }

    private fun getUserIdFromSharedPreferences(): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
}