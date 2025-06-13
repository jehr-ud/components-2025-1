package com.ud.hangedgame.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ud.hangedgame.repositories.LevelRepository
import com.ud.hangedgame.repositories.ScoreRepository

class LevelViewModelFactory(
    private val scoreRepository: ScoreRepository,
    private val repository: LevelRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LevelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LevelViewModel(scoreRepository, repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}