package com.ud.hangedgame.providers.network

import com.ud.hangedgame.models.LevelsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LevelApiService {
    @GET("api/levels/user")
    suspend fun getUserLevels(@Query("score") score: Int): Response<LevelsResponse>
}