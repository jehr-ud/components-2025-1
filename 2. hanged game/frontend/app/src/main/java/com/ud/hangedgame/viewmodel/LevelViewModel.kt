package com.ud.hangedgame.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.hangedgame.models.Level
import com.ud.hangedgame.repositories.LevelRepository
import com.ud.hangedgame.repositories.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest // Importa collectLatest
import kotlinx.coroutines.launch

class LevelViewModel(
    private val scoreRepository: ScoreRepository,
    private val repository: LevelRepository,
    private val context: Context) : ViewModel() {
    private val PREFS_NAME = "VocaUDPrefs"
    private val KEY_USER_ID = "userId"

    private val _levels = MutableLiveData<List<Level>>()
    val levels: LiveData<List<Level>> = _levels

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _totalScore = MutableStateFlow(0)
    val totalScore: StateFlow<Int> = _totalScore.asStateFlow()

    init {
        loadTotalScore()
    }

    fun loadTotalScore() {
        _isLoading.value = true
        val userId = getUserIdFromSharedPreferences()
        if (userId != null) {
            viewModelScope.launch {
                scoreRepository.getTotalScoreForUser(userId).collectLatest { score ->
                    _totalScore.value = score
                    fetchUserLevels(score)
                }
            }
        } else {
            println("No se pudo obtener el userId de SharedPreferences en LevelViewModel.")
            _totalScore.value = 0
        }
    }

    fun fetchUserLevels(score: Int) {
        viewModelScope.launch {
            try {
                Log.d("vocaud", "get levels ${score}")
                val response = repository.getUserLevels(score)
                if (response.isSuccessful) {
                    _levels.value = response.body()?.allLevels ?: emptyList()
                } else {
                    _errorMessage.value = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
                Log.d("vocaud", "error ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getUserIdFromSharedPreferences(): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
}