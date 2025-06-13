package com.ud.hangedgame.repositories


import com.ud.hangedgame.models.LevelsResponse
import com.ud.hangedgame.providers.network.LevelApiService
import retrofit2.Response

class LevelRepository(private val apiService: LevelApiService) {

    suspend fun getUserLevels(score: Int): Response<LevelsResponse> {
        return apiService.getUserLevels(score)
    }
}