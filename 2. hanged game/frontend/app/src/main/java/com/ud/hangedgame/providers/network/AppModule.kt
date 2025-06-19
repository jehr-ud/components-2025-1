package com.ud.hangedgame.providers.network

import com.ud.hangedgame.repositories.LevelRepository
import com.ud.hangedgame.viewmodel.LevelViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {

    private const val BASE_URL = "http://localhost:5000/" // base URL

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val levelApiService: LevelApiService by lazy {
        retrofit.create(LevelApiService::class.java)
    }

    val levelRepository: LevelRepository by lazy {
        LevelRepository(levelApiService)
    }
}