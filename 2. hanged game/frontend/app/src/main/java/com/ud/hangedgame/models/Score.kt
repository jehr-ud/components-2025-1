// src/main/java/com/ud/hangedgame/models/Score.kt
package com.ud.hangedgame.models

data class Score(
    val scoreValue: Int = 0, // 0 errores significa un score perfecto
    val level: String = "",
    val timestamp: Long = System.currentTimeMillis()
)