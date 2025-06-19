package com.ud.hangedgame.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.hangedgame.models.Match
import com.ud.hangedgame.repositories.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val PREFS_NAME = "VocaUDPrefs"
private val KEY_USER_ID = "userId"

data class RoomUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val matchCreatedId: String? = null,
    val matchJoined: Match? = null
)

class RoomViewModel : ViewModel() {

    private val repository = MatchRepository()

    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState = _uiState.asStateFlow()

    private fun getUserId(context: Context): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun createNewMatch(context: Context) {
        viewModelScope.launch {
            _uiState.value = RoomUiState(isLoading = true)
            val userId = getUserId(context)
            if (userId == null) {
                _uiState.value = RoomUiState(error = "Usuario no identificado.")
                return@launch
            }

            val matchId = repository.createMatch(userId)
            if (matchId != null) {
                _uiState.value = RoomUiState(matchCreatedId = matchId)
                listenToMatch(matchId)
            } else {
                _uiState.value = RoomUiState(error = "No se pudo crear la partida.")
            }
        }
    }

    fun joinExistingMatch(context: Context, matchId: String) {
        if (matchId.isBlank()) {
            _uiState.value = RoomUiState(error = "El ID de la sala no puede estar vacío.")
            return
        }

        viewModelScope.launch {
            _uiState.value = RoomUiState(isLoading = true)
            val userId = getUserId(context)
            if (userId == null) {
                _uiState.value = RoomUiState(error = "Usuario no identificado.")
                return@launch
            }

            val success = repository.joinMatch(matchId, userId)
            if (success) {
                listenToMatch(matchId)
            } else {
                _uiState.value = RoomUiState(error = "No se pudo unir a la partida. Puede que esté llena o no exista.")
            }
        }
    }

    private fun listenToMatch(matchId: String) {
        viewModelScope.launch {
            repository.listenForMatchUpdates(matchId).collectLatest { match ->
                _uiState.value = RoomUiState(isLoading = false, matchJoined = match)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetNavigation() {
        _uiState.value = _uiState.value.copy(matchCreatedId = null, matchJoined = null)
    }
}