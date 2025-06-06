package com.ud.hangedgame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.hangedgame.repositories.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
private val wordRepository: WordRepository = WordRepository()
) : ViewModel() {

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

    private val MAX_ERRORS = 5

    fun loadNewWord(level: String) {
        _isLoading.value = true
        wordRepository.getRandomWordByLevel(level) { word, exception ->
            if (word != null) {
                _secretWord.value = word.word?.lowercase()
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
}