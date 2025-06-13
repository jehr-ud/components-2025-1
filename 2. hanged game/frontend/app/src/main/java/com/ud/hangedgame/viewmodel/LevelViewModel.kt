package com.ud.hangedgame.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.hangedgame.repositories.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest // Importa collectLatest
import kotlinx.coroutines.launch

class LevelViewModel(
    private val scoreRepository: ScoreRepository,
    private val context: Context
) : ViewModel() {

    private val PREFS_NAME = "VocaUDPrefs"
    private val KEY_USER_ID = "userId"

    private val _totalScore = MutableStateFlow(0)
    val totalScore: StateFlow<Int> = _totalScore.asStateFlow()

    init {
        // Al inicializar el ViewModel, intenta cargar el score del usuario
        loadTotalScore()
    }

    fun loadTotalScore() {
        val userId = getUserIdFromSharedPreferences()
        if (userId != null) {
            viewModelScope.launch {
                scoreRepository.getTotalScoreForUser(userId).collectLatest { score ->
                    _totalScore.value = score
                }
            }
        } else {
            println("No se pudo obtener el userId de SharedPreferences en LevelViewModel.")
            _totalScore.value = 0 // Si no hay usuario, el score es 0
        }
    }

    private fun getUserIdFromSharedPreferences(): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
}